package com.yilin.netty.http;

import org.apache.log4j.Logger;

import com.yilin.netty.commons.constant.GatewayConfig;
import com.yilin.netty.commons.utils.DesUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

/**
 * 
 */
public class HttpBackendHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

	Logger logger = Logger.getLogger(getClass());

	private final Channel inboundChannel;

	public HttpBackendHandler(Channel inboundChannel) {
		this.inboundChannel = inboundChannel;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Backend Handler is Active!");
		super.channelActive(ctx);
	}

	@Override
	public void channelRead0(final ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
		String responseContent = getResponseContent(msg);
		int unEncryptLength = responseContent.getBytes().length;
		logger.info("返回明文："+responseContent);
		try {
			responseContent = new String(DesUtils.encrypt(responseContent, GatewayConfig.DES_KEY));
			logger.info("返回密文："+responseContent);
		} catch (Exception e) {
			logger.warn("请求参数加密失败");
		}
		byte[] bytes = responseContent.getBytes();
		int encryptLength = bytes.length;
		msg.headers().set("Content-Length", encryptLength);
		msg.headers().set("Content-Type", "application/json; charset=UTF-8");
		if (unEncryptLength < encryptLength) {
			msg.content().capacity(encryptLength);
			msg.content().resetWriterIndex();
			msg.content().writeBytes(bytes);
//			msg.content().setBytes(0, bytes);
		} else {
			msg.content().setBytes(0, bytes);
		}
		inboundChannel.writeAndFlush(msg.retain()).addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if (!future.isSuccess()) {
					future.channel().close();
				} else {
					System.out.println("HttpBackendHandler->channelRead0->!future.isSuccess()");
				}
			}
		});
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		System.out.println("Backend Handler destroyed!");
		HttpFrontendHandler.closeOnFlush(inboundChannel);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		HttpFrontendHandler.closeOnFlush(ctx.channel());
	}

	/**
	 * 获取请求的内容
	 * 
	 * @param request
	 * @return
	 */
	private String getResponseContent(FullHttpResponse response) {
		ByteBuf contentBuf = response.content();
		String content = contentBuf.toString(CharsetUtil.UTF_8);
		return content;
	}

}
