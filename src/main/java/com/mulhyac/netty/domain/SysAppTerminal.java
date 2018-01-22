package com.mulhyac.netty.domain;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

/**
 * 系统应用终端
 */
@Table("sys_app_terminal")
public class SysAppTerminal {

	/**
	 * 序号
	 */
	@Id
	@Column("id")
	private Integer id;

	/**
	 * 应用版本
	 */
	@Column("soft_type")
	private String softType;

	/**
	 * 产品名称
	 */
	@Column("app_name")
	private String appName;

	/**
	 * 版本号
	 */
	@Column("version")
	private String version;

	/**
	 * 手机号码
	 */
	@Column("app_key")
	private String appKey;

	/**
	 * 反馈内容
	 */
	@Column("app_url")
	private String appUrl;

	/**
	 * 创建时间
	 */
	@Column("is_delete")
	private Integer isDelete;

	/**
	 * 修改时间
	 */
	@Column("create_time")
	private Date createTime;

	/**
	 * 是否处理
	 */
	@Column("update_time")
	private Date updateTime;

	/**
	 * 是否启用
	 */
	@Column("is_forced")
	private Integer isForced;

	/**
	 * user_name
	 */
	@Column("user_name")
	private String userName;

	/**
	 * 描述
	 */
	@Column("description")
	private String description;

	/**
	 * 是否发布
	 */
	@Column("is_released")
	private Integer isReleased;

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

	public void setSoftType(String softType) {
		this.softType = softType;
	}

	public String getSoftType() {
		return this.softType;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppName() {
		return this.appName;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return this.version;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppKey() {
		return this.appKey;
	}

	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}

	public String getAppUrl() {
		return this.appUrl;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public Integer getIsDelete() {
		return this.isDelete;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setIsForced(Integer isForced) {
		this.isForced = isForced;
	}

	public Integer getIsForced() {
		return this.isForced;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public void setIsReleased(Integer isReleased) {
		this.isReleased = isReleased;
	}

	public Integer getIsReleased() {
		return this.isReleased;
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