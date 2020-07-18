package io.github.ramerf.blog.system.config;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import io.github.ramerf.blog.system.entity.Constant.PrivilegeEnum;
import io.github.ramerf.blog.system.entity.domain.common.Manager;
import io.github.ramerf.blog.system.exception.CommonException;
import io.github.ramerf.blog.system.service.common.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/** Created by RAMER on 5/22/2017. */
@Service
@Slf4j
public class CustomUserService implements UserDetailsService {
  @Resource private ManagerService managerService;
  @Resource private RoleService rolesService;
  @Resource private PrivilegeService privilegeService;

  @Override
  public UserDetails loadUserByUsername(String empNo) throws UsernameNotFoundException {
    AtomicLong during = new AtomicLong();
    boolean allowLogin =
        Optional.ofNullable(managerService.getLoginStatus(empNo))
            .map(
                managerLogin -> {
                  if (managerLogin.getCount() >= 3) {
                    during.set(managerLogin.getDuring());
                    return false;
                  }
                  return true;
                })
            .orElse(true);
    if (!allowLogin) {
      throw CommonException.of(
          "错误次数过多,请" + ((during.get() - System.currentTimeMillis()) / (1000 * 60)) + "分钟后再试");
    }
    Manager manager = managerService.getByEmpNo(empNo);
    log.debug("loadUserByUsername  empNo: [{}]", empNo);
    if (manager == null) {
      log.debug("loadUserByUsername  empNo not exist: [{}]", empNo);
      throw new UsernameNotFoundException("empNo not found : " + empNo);
    }
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    final Set<String> privilegeSuffixes = PrivilegeEnum.map().keySet();
    rolesService
        .listByManager(manager.getId())
        .forEach(
            role -> {
              authorities.add(new SimpleGrantedAuthority(role.getName()));
              privilegeService
                  .listByRoles(role.getId())
                  .forEach(
                      privilege -> {
                        String privilegeName = privilege.getExp();
                        if (privilegeName.contains("*")) {
                          authorities.add(new SimpleGrantedAuthority(privilegeName));
                          for (String privilegeSuffix : privilegeSuffixes) {
                            authorities.add(
                                new SimpleGrantedAuthority(
                                    privilegeName.substring(0, privilegeName.indexOf("*"))
                                        + privilegeSuffix));
                          }
                        } else {
                          if (privilegeSuffixes.contains(
                              privilegeName.substring(privilegeName.lastIndexOf(":") + 1))) {
                            authorities.add(new SimpleGrantedAuthority(privilegeName));
                          }
                        }
                      });
            });
    log.debug("loadUserByUsername permission : {}", authorities);
    return new User(manager.getEmpNo(), manager.getPassword(), authorities);
  }
}
