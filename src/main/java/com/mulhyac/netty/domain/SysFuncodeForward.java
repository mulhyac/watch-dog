package com.mulhyac.netty.domain;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

/**
 * 功能代码及转发跳转
 */
@Table("sys_funcode_forward")
public class SysFuncodeForward {

	/**
	 * 标识
	 */
	@Id
	@Column("id")
	private Integer id;

	/**
	 * 功能代码
	 */
	@Name
	@Column("fun_code")
	private String funCode;

	/**
	 * 主机名
	 */
	@Column("host")
	private String host;

	/**
	 * 端口
	 */
	@Column("port")
	private String port;

	/**
	 * 转发的url
	 */
	@Column("forward_url")
	private String forwardUrl;

	/**
	 * 是否启用
	 */
	@Column("is_forced")
	private Integer isForced;

	/**
	 * 是否校验token
	 */
	@Column("is_token")
	private Integer isToken;

	/**
	 * ext_1
	 */
	@Column("ext_1")
	private String ext1;

	/**
	 * ext_2
	 */
	@Column("ext_2")
	private String ext2;

	/**
	 * ext_3
	 */
	@Column("ext_3")
	private String ext3;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public void setFunCode(String funCode) {
		this.funCode = funCode;
	}

	public String getFunCode() {
		return this.funCode;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHost() {
		return this.host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPort() {
		return this.port;
	}

	public void setForwardUrl(String forwardUrl) {
		this.forwardUrl = forwardUrl;
	}

	public String getForwardUrl() {
		return this.forwardUrl;
	}

	public void setIsForced(Integer isForced) {
		this.isForced = isForced;
	}

	public Integer getIsForced() {
		return this.isForced;
	}

	public void setIsToken(Integer isToken) {
		this.isToken = isToken;
	}

	public Integer getIsToken() {
		return this.isToken;
	}

	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}

	public String getExt1() {
		return this.ext1;
	}

	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}

	public String getExt2() {
		return this.ext2;
	}

	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}

	public String getExt3() {
		return this.ext3;
	}
}