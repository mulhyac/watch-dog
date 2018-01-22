package com.mulhyac.netty.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import com.mulhyac.netty.http.config.Environment;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

import com.mulhyac.netty.commons.constant.GatewayConfig;
import com.mulhyac.netty.commons.utils.DesUtils;
import com.mulhyac.netty.domain.SysAppTerminal;
import com.mulhyac.netty.domain.SysFuncodeForward;
import com.mulhyac.plugin.spring.support.SpringUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import redis.clients.jedis.JedisCluster;

/**
 * 
 */
public class HttpFrontendHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	Logger logger = Logger.getLogger(getClass());

	private Channel outboundChannel;

	Map<String, String> errors = (Map<String, String>) Environment.get("http.status");

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("FrontEnd Handler is Active!");
		super.channelActive(ctx);
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		if (request.method() != HttpMethod.POST) {
			responseJson(ctx, request, getResponse("9999"));
			logger.warn("不支持get方法");
			return;
		}
		String requestContent = getRequestContent(request);
		if (requestContent.indexOf("%") > -1) {
			requestContent = URLDecoder.decode(requestContent, "UTF-8");
		}
		logger.debug("请求未解密参数:" + requestContent);
		try {
			requestContent = new String(DesUtils.decrypt(requestContent, GatewayConfig.DES_KEY));
		} catch (Exception e) {
			logger.warn("请求参数解密失败");
			responseJson(ctx, request, getResponse("9999"));
			return;
		}
		logger.debug("请求参数解密:" + requestContent);
		NutMap json = mapToObj(ctx, request, requestContent);
		if (json == null) {
			responseJson(ctx, request, getResponse("9999"));
			logger.warn("无效的json字符串");
			return;
		}
		String funCode = json.getString("funCode");
		SysAppTerminal sysAppTerminal = getSysAppTerminal(ctx, request, json);
		if ("100001".equals(funCode)) {
			NutMap update = bootstrapCheckUpdate(ctx, request, sysAppTerminal, json);
			responseJson(ctx, request, update);
			return;
		}
		if ("100002".equals(funCode)) {
			bootstrapExit(ctx, request, json);
			return;
		}
		NutMap check = bootstrapCheckUpdate(ctx, request, sysAppTerminal, json);
		if (!check.getString("retCode").equals("0000")) {
			logger.warn("请求异常，版本信息未通过检查：" + Json.toJson(json));
			unchecked(ctx, request, check);
			return;
		}
		SysFuncodeForward sysFuncodeForward = getFunc(funCode);
		check = checkFunc(sysFuncodeForward, json);
		if (sysFuncodeForward == null) {
			logger.warn("无效功能代码：" + funCode);
			responseJson(ctx, request, getResponse("9999"));
			return;
		}
		if (!check.getString("retCode").equals("0000")) {
			logger.warn("请求异常，请求参数：" + Json.toJson(json));
			unchecked(ctx, request, check);
			return;
		}
		String host = sysFuncodeForward.getHost();
		int port = Integer.parseInt(sysFuncodeForward.getPort().trim());
		String uri = sysFuncodeForward.getForwardUrl();
		byte[] bytes = requestContent.getBytes();
		request.headers().set("Content-Length", bytes.length);
		request.headers().set("Content-Type", "application/json; charset=UTF-8");
		request.content().setBytes(0, bytes);
		bootstrap(ctx, request, host, port, uri);
	}

	private SysAppTerminal getSysAppTerminal(ChannelHandlerContext ctx, FullHttpRequest request, NutMap json) {
		String softType = json.getString("softType");
		String version = json.getString("version");
		String redisKey = "APP_" + softType + "_" + version;
		JedisCluster jedisCluster = SpringUtils.getBean("jedisCluster");
		String redisValue = jedisCluster.get(redisKey);
		if (redisValue != null) {
			return Json.fromJson(SysAppTerminal.class, redisValue);
		}
		if (softType == null || version == null) {
			logger.warn("无效softType[" + softType + "]或无效version[" + version + "]");
			return null;
		}
		Dao nutzDao = SpringUtils.getBean("nutzDao");
		SysAppTerminal sysAppTerminal = nutzDao.fetch(SysAppTerminal.class, Cnd.where("soft_type", "=", softType).and("version", "=", version));
		return sysAppTerminal;
	}

	private void unchecked(ChannelHandlerContext ctx, FullHttpRequest request, NutMap check) {
		try {
			logger.info("unchecked:" + check.get("retCode") + ":" + check.get("retMsg"));
			if (StringUtils.isBlank(check.getString("retCode"))) {
				responseJson(ctx, request, getResponse("9999"));
			} else {
				responseJson(ctx, request, getResponse(check.getString("retCode")));
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * 系统退出功能 功能码：100002
	 */
	public void bootstrapExit(ChannelHandlerContext ctx, FullHttpRequest request, NutMap json) {
		String userId = json.getString("userId");
		String userToken = json.getString("tokenId");
		try {
			if (userId == null) {
				responseJson(ctx, request, getResponse("9999"));
				logger.error("bootstrapExit:" + userId);
				return;
			}
			String tokenId = "crm_unified_user_token_" + userId;
			JedisCluster jedisCluster = SpringUtils.getBean("jedisCluster");
			String redisToken = jedisCluster.get(tokenId);
			if (userToken != null && redisToken != null && userToken.equals(redisToken)) {
				Long del = jedisCluster.del(tokenId);
				if (del > 0) {
					responseJson(ctx, request, getResponse("0000"));
					return;
				}
			}
			responseJson(ctx, request, getResponse("6666"));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("bootstrapExit:" + e.getMessage());
		}
		return;
	}

	/**
	 * 检查更新 功能码：100001
	 */
	public NutMap bootstrapCheckUpdate(ChannelHandlerContext ctx, FullHttpRequest request, SysAppTerminal sysAppTerminal, NutMap json) {
		NutMap response = new NutMap();
		if (null == sysAppTerminal) {
			// 查不到对应版本信息，返回系统异常
			response.addv("retCode", "9999");
			response.addv("retMsg", "系统异常");
			return response;
		} else {
			NutMap retData = new NutMap();
			SysAppTerminal lastVersion = getLastVersion(sysAppTerminal.getSoftType());
			if (lastVersion != null && lastVersion.getId() == sysAppTerminal.getId()) {
				response.addv("retCode", "0000");
				retData.addv("versionName", "客户端已是最高版本");
				retData.addv("sign", "0");
				response.addv("retData", retData);
				return response;
			} else {
				retData.put("versionName", lastVersion.getAppName());
				retData.put("url", lastVersion.getAppUrl());
				retData.put("description", lastVersion.getDescription());
				retData.put("sign", sysAppTerminal.getIsDelete());
				if (0 == sysAppTerminal.getIsForced()) {
					// 未启用，需要强更
					response.put("retCode", "0000");
					retData.put("sign", 2);
				} else if (0 == sysAppTerminal.getIsReleased()) {
					// 未发布，系统异常
					response.put("retCode", "9999");
				} else {
					// 正常
					response.put("retCode", "0000");
				}
				response.put("retData", retData);
				return response;
			}
		}
	}

	public SysAppTerminal getLastVersion(String soft_type) {
		Dao nutzDao = SpringUtils.getBean("nutzDao");
		return nutzDao.fetch(SysAppTerminal.class, Cnd.where("soft_type", "=", soft_type).desc("id"));
	}

	public void bootstrap(final ChannelHandlerContext ctx, final FullHttpRequest request, String host, int port, String uri) {
		Channel inboundChannel = ctx.channel();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(inboundChannel.eventLoop()).channel(ctx.channel().getClass()).handler(new BackendHandlerInitializer(inboundChannel));
		ChannelFuture f = bootstrap.connect(host, port);
		outboundChannel = f.channel();
		request.retain();
		request.setUri(uri);
		f.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					outboundChannel.writeAndFlush(request);
				} else {
					inboundChannel.close();
				}
			}
		});
	}

	public NutMap jsonToObj(ChannelHandlerContext ctx, FullHttpRequest request, String requestContent) {
		NutMap requestJson = null;
		try {
			requestJson = Json.fromJson(NutMap.class, requestContent);
		} catch (Exception e) {
			try {
				responseJson(ctx, request, getResponse("10001"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
		return requestJson;
	}

	public NutMap mapToObj(ChannelHandlerContext ctx, FullHttpRequest request, String requestContent) {
		NutMap requestJson = null;
		requestJson = Json.fromJson(NutMap.class, requestContent);
		// try {
		// if (requestContent == null || requestContent.indexOf("=") == -1) {
		// return null;
		// }
		// requestJson = new NutMap();
		// for (String line : requestContent.split("&")) {
		// String[] cells = line.split("=");
		// if (cells.length == 2) {
		// requestJson.addv(cells[0], cells[1]);
		// } else {
		// requestJson.addv(cells[0], "");
		// }
		// }
		// } catch (Exception e) {
		// try {
		// logger.info("请求参数错误：" + requestJson);
		// responseJson(ctx, request, getResponse("10003"));
		// } catch (UnsupportedEncodingException e1) {
		// e1.printStackTrace();
		// }
		// }
		return requestJson;
	}

	public NutMap checkFunc(SysFuncodeForward sysFuncodeForward, NutMap json) {
		NutMap check = new NutMap();
		check.put("retCode", "0000");
		if (sysFuncodeForward == null) {
			check.put("retCode", "9999");
			check.put("retMsg", errors.get("9999"));
			return check;
		}
		if (sysFuncodeForward.getIsForced() == 0) {
			check.put("retCode", "9999");
			check.put("retMsg", errors.get("9999"));
			return check;
		}
		if (1 == sysFuncodeForward.getIsToken()) {
			String userId = json.getString("userId");
			if (userId == null) {
				check.put("retCode", "9999");
				check.put("retMsg", errors.get("9999"));
				return check;
			}
			String tokenKey = "crm_unified_user_token_" + userId;
			String tokenId = json.getString("tokenId");
			JedisCluster jedisCluster = SpringUtils.getBean("jedisCluster");
			String token = jedisCluster.get(tokenKey) + "";

			if (token == null || (!token.equals(tokenId))) {
				logger.info("请求失败，无效tonenId：" + tokenId);
				check.put("retCode", "6666");
				check.put("retMsg", errors.get("6666"));
				return check;
			}
		}
		return check;
	}

	public SysFuncodeForward getFunc(String funCode) {
		if (funCode == null) {
			return null;
		}
//		String redisKey = "FUNCODE_FORWARD_" + funCode;
		// JedisCluster jedisCluster = SpringUtils.getBean("jedisCluster");
		// String redisValue = jedisCluster.get(redisKey);
		// if (redisValue != null) {
		// return Json.fromJson(SysFuncodeForward.class, redisValue);
		// }
		Dao nutzDao = SpringUtils.getBean("nutzDao");
		SysFuncodeForward func = nutzDao.fetch(SysFuncodeForward.class, Cnd.where("FUN_CODE", "=", funCode));
		return func;
	}

	private String getResponse(String error) {
		NutMap map = new NutMap();
		map.addv("retCode", error);
		map.addv("retMsg", errors.get(error));
		return Json.toJson(map);
	}

	/**
	 * 响应HTTP的请求
	 * 
	 * @param ctx
	 * @param req
	 * @param jsonStr
	 * @throws UnsupportedEncodingException
	 */
	private void responseJson(ChannelHandlerContext ctx, FullHttpRequest req, String jsonStr) throws UnsupportedEncodingException {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(jsonStr.getBytes("utf-8")));
		response.headers().set("Content-Type", "text/xml; charset=UTF-8");
		logger.warn("网关拦截：" + jsonStr);
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * 响应HTTP的请求
	 * 
	 * @param ctx
	 * @param req
	 * @param obj
	 * @throws UnsupportedEncodingException
	 */
	private void responseJson(ChannelHandlerContext ctx, FullHttpRequest req, Object obj) throws UnsupportedEncodingException {
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(Json.toJson(obj).getBytes("utf-8")));
		response.headers().set("Content-Type", "text/xml; charset=UTF-8");
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}

	/**
	 * 获取请求的内容
	 * 
	 * @param request
	 * @return
	 */
	private String getRequestContent(FullHttpRequest request) {
		ByteBuf contentBuf = request.content();
		String content = contentBuf.toString(CharsetUtil.UTF_8);
		return content;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		if (outboundChannel != null) {
			closeOnFlush(outboundChannel);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		closeOnFlush(ctx.channel());
	}

	static void closeOnFlush(Channel ch) {
		if (ch.isActive()) {
			ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
		}
	}
}
