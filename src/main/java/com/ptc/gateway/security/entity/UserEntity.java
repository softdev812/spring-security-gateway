package com.ptc.gateway.security.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ptc.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author peter
 *
 */
@Data
public class UserEntity implements Serializable, UserDetails {
	private static final long serialVersionUID = 1L;


	/**
	 * UserID
	 */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "UserID")
	@Excel(name = "UserID")
	private Long userId;

	/**
	 * DeptID
	 */
	@ApiModelProperty(value = "DeptID")
	@Excel(name = "DeptID")
	private Long deptId;

	/**
	 * DeptName
	 */
	@ApiModelProperty(value = "DeptName")
	@TableField(exist = false)
	private String deptName;

	/**
	 * Postid
	 */
	@ApiModelProperty(value = "Postid")
	@Excel(name = "Postid")
	private Long postId;

	/**
	 * UserAccount
	 */
	@ApiModelProperty(value = "UserAccount")
	@Excel(name = "UserAccount")
	private String userName;

	/**
	 * UserNickname
	 */
	@ApiModelProperty(value = "UserNickname")
	@Excel(name = "UserNickname")
	private String nickName;

	/**
	 * UserType（0: SystemUser）
	 */
	@ApiModelProperty(value = "UserType（0: SystemUser）")
	@Excel(name = "UserType（0: SystemUser）")
	private String userType;

	/**
	 * UserEmail
	 */
	@ApiModelProperty(value = "UserEmail")
	@Excel(name = "UserEmail")
	private String email;

	/**
	 * Mobile
	 */
	@ApiModelProperty(value = "Mobile")
	@Excel(name = "Mobile")
	private String phonenumber;

	/**
	 * UserSex
	 */
	@ApiModelProperty(value = "UserSex")
	@Excel(name = "UserSex")
	private Integer sex;

	/**
	 * avatar
	 */
	@ApiModelProperty(value = "avatar")
	@Excel(name = "avatar")
	private String avatar;

	/**
	 * password
	 */
	@ApiModelProperty(value = "password")
	@Excel(name = "password")
	private String password;

	/**
	 * status 0:normal, 1:deny
	 */
	@ApiModelProperty(value = "status 0:normal, 1:deny")
	@Excel(name = "status 0:normal, 1:deny")
	private Integer status;

	/**
	 * delete 0:exist, 2:deleted
	 */
	@ApiModelProperty(value = "delete 0:exist, 2:deleted")
	@Excel(name = "delete 0:exist, 2:deleted")
	private Integer delFlag;

	/**
	 * latestLoginIP
	 */
	@ApiModelProperty(value = "latestLoginIP")
	@Excel(name = "latestLoginIP")
	private String loginIp;

	/**
	 * latestLoginTime
	 */
	@ApiModelProperty(value = "latestLoginTime")
	@Excel(name = "latestLoginTime", width = 30, dateFormat = "yyyy-MM-dd")
	private Date loginDate;

	/**
	 * createdUser
	 */
	@ApiModelProperty(value = "createdUser")
	@Excel(name = "createdUser")
	private Long createBy;

	/**
	 * createTime
	 */
	@ApiModelProperty(value = "createTime")
	@Excel(name = "createTime", width = 30, dateFormat = "yyyy-MM-dd")
	private Date createTime;

	/**
	 * updatedUser
	 */
	@ApiModelProperty(value = "updatedUser")
	@Excel(name = "updatedUser")
	private Long updateBy;

	/**
	 * updateTime
	 */
	@ApiModelProperty(value = "updateTime")
	@Excel(name = "updateTime", width = 30, dateFormat = "yyyy-MM-dd")
	private Date updateTime;

	/**
	 * comment
	 */
	@ApiModelProperty(value = "comment")
	@Excel(name = "comment")
	private String remark;


	/**
	 * roleId
	 */
	@ApiModelProperty(value = "roleId")
	@TableField(exist = false)
	@Excel(name = "roleId")
	private Integer roleId;

	/**
	 * roleName
	 */
	@ApiModelProperty(value = "roleName")
	@TableField(exist = false)
	@Excel(name = "roleName")
	private String roleName;

	@ApiModelProperty(value = "perm")
	@TableField(exist = false)
	@Excel(name = "perm")
	private List<String> rights;


	@Override
	public String getUsername() {
		return this.userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.rights.stream().filter(item-> !item.isEmpty()).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return this.password;
	}
}
