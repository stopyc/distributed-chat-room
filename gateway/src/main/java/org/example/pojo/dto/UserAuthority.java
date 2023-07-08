package org.example.pojo.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YC104
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserAuthority implements UserDetails {

    private static final long serialVersionUID = -7537158683449986394L;
    /**
     * 用户信息
     */
    private UserDTO user;

    /**
     * 用户权限
     */
    private List<String> permissions;

    /**
     * 封装后的用户权限
     */
    @JSONField(serialize = false)
    private List<SimpleGrantedAuthority> authorities;

    public UserAuthority(UserDTO user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    /**
     * 重写用户权限获取
     *
     * @return 用户权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authorities != null) {
            return authorities;
        }
        // 把 permissions 中 String 类型的权限信息封装成 SimpleGrantedAuthority 对象
        authorities = permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
}