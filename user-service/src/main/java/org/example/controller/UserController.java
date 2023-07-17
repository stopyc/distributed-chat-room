package org.example.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.UserServiceApplication;
import org.example.annotation.Inner;
import org.example.pojo.bo.UserBO;
import org.example.pojo.dto.ResultDTO;
import org.example.pojo.dto.UserAuthority;
import org.example.pojo.exception.BusinessException;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.vo.JavaFileVO;
import org.example.pojo.vo.ResultVO;
import org.example.pojo.vo.UserVO;
import org.example.service.UserService;
import org.example.util.FileUtils;
import org.example.util.RequestHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import static org.example.constant.GlobalConstants.*;
import static org.example.constant.ResultEnum.*;

/**
 * @author YC104
 */
@RestController
@Slf4j
@RequestMapping(value = "user", produces = "application/json;charset=UTF-8")
public class UserController {

    @Resource
    private HttpServletRequest httpServletRequest;

    @Resource
    private UserService userService;

    @Value("${spring.application.name}")
    private String currentApplicationName;


    @GetMapping(value = "/r1")
    //@PreAuthorize("hasAnyAuthority('p2')")
    @PreAuthorize("@ss.hasPermi('system:admin:add')")
    public String r1() {
        //获取用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAuthority user = (UserAuthority) authentication.getPrincipal();
        UserDTO user1 = user.getUser();
        System.out.println("user1 = " + user1);
        System.out.println("user = " + user);

        return "访问资源1";
    }

    @GetMapping("/public/list")
    public String r2() {
        return "这是一个公共资源";
    }

    @GetMapping("/inner/getUserByUsername")
    @Inner
    public ResultVO getUserByUsername(@RequestParam("username") String username) {

        if (!StringUtils.hasText(username)) {
            username = RequestHolder.get().getIp();
        }

        return userService.getUserByUsername(username);
    }

    @PutMapping("/logout")
    public ResultVO logout() {
        return userService.logout();
    }

    @GetMapping("/public/runpath")
    public ResultVO getRunPath() {

        try {

            ProtectionDomain domain = UserServiceApplication.class.getProtectionDomain();
            CodeSource codeSource = domain.getCodeSource();
            URI location = codeSource.getLocation().toURI();
            File jarFile;

            System.out.println("location.getScheme() = " + location.getScheme());


            if ("file".equals(location.getScheme())) {
                //当前class文件存放位置
                String classLocationPath = location.getPath();

                System.out.println("classLocationPath = " + classLocationPath);

                //复制出来一份class文件
                String destClassLocationPath = location.getPath().split("classes")[0] + currentApplicationName;

                System.out.println("destClassLocationPath = " + destClassLocationPath);

                jarFile = new File(location);

                File copy = FileUtil.copy(jarFile, new File(destClassLocationPath), true);

                //获取到所有的目标文件,是文件,并且以class结尾
                List<File> fileList = FileUtils.traverFolder(destClassLocationPath, ".class");

                fileList
                        .forEach(file -> {
                            String javaFileDestPath = javaFileDestPath = file.getPath().split(file.getName())[0];
                            String cmd = "java -jar F:\\反编译java\\procyon-decompiler-0.6.0.jar  " + file.getPath() + " -o " + javaFileDestPath;
                            try {
                                log.info("cmd 为: {}", cmd);
                                Process process = Runtime.getRuntime().exec(cmd);
                            } catch (IOException e) {
                            }
                        });
            } else if ("jar".equals(location.getScheme())) {
                //当前class文件存放位置
                String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

                System.out.println("path = " + path);

                File file = new File(path);

                String destPath = path.split("classes!/")[0];
                System.out.println("destPath = " + destPath);

                File file1 = new File(destPath);
                String parent = file1.getParent().split("!")[0].replace("file:" + File.separator, "");
                String parent1 = file1.getParentFile().getParent().replace("file:" + File.separator, "");
                String dest = parent1 + File.separator + currentApplicationName + File.separator + currentApplicationName + ".jar";
                System.out.println("dest = " + dest);

                FileUtil.copy(new File(parent), new File(dest), true);
                System.out.println("parent = " + parent);

                ZipUtil.unzip(dest, parent1 + File.separator + currentApplicationName);

                //获取到所有的目标文件,是文件,并且以class结尾
                List<File> fileList = FileUtils.traverFolder(parent1 + File.separator + currentApplicationName, ".class");

                fileList
                        .forEach(file2 -> {
                            String javaFileDestPath = javaFileDestPath = file2.getPath().split(file2.getName())[0];
                            String cmd = "java -jar F:\\反编译java\\procyon-decompiler-0.6.0.jar  " + file2.getPath() + " -o " + javaFileDestPath;
                            try {
                                log.info("cmd 为: {}", cmd);
                                Process process = Runtime.getRuntime().exec(cmd);
                            } catch (IOException e) {
                            }
                        });
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultVO.ok();
    }


    @PostMapping("/public/getJavaFile")
    public ResultVO getJavaFile(@RequestBody JavaFileVO javaFileVO) {
        try {

            ProtectionDomain domain = UserServiceApplication.class.getProtectionDomain();
            CodeSource codeSource = domain.getCodeSource();
            URI location = codeSource.getLocation().toURI();
            File jarFile;

            System.out.println("location.getScheme() = " + location.getScheme());


            if ("file".equals(location.getScheme())) {
                //当前class文件存放位置
                String classLocationPath = location.getPath();

                System.out.println("classLocationPath = " + classLocationPath);

                //复制出来一份class文件
                String destClassLocationPath = location.getPath().split("classes")[0] + currentApplicationName;

                System.out.println("destClassLocationPath = " + destClassLocationPath);

                jarFile = new File(location);

                File copy = FileUtil.copy(jarFile, new File(destClassLocationPath), true);

                //获取到所有的目标文件,是文件,并且以class结尾
                List<File> fileList = FileUtils.traverFolder(destClassLocationPath, ".class");

            } else if ("jar".equals(location.getScheme())) {
                //当前class文件存放位置
                String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

                System.out.println("path = " + path);

                File file = new File(path);

                String destPath = path.split("classes!/")[0];
                System.out.println("destPath = " + destPath);

                File file1 = new File(destPath);
                String parent = file1.getParent().split("!")[0].replace("file:" + File.separator, "");
                String parent1 = file1.getParentFile().getParent().replace("file:" + File.separator, "");
                String dest = parent1 + File.separator + currentApplicationName + File.separator + currentApplicationName + ".jar";
                System.out.println("dest = " + dest);

                FileUtil.copy(new File(parent), new File(dest), true);
                System.out.println("parent = " + parent);

                ZipUtil.unzip(dest, parent1 + File.separator + currentApplicationName);

                //获取到所有的目标文件,是文件,并且以class结尾
                //List<File> fileList = FileUtils.traverFolder(, ".class");
                //FileUtils.findFile(parent1 + File.separator + currentApplicationName, )


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @PostMapping("/register")
    public ResultVO register(@Validated @RequestBody UserVO userVO) {
        return userService.register(userVO);
    }

    @GetMapping("/public/xss")
    public String testXss(String data) {
        return data;
    }

    @GetMapping("/inner/getById")
    @Inner
    public ResultDTO getById(@RequestParam("userId") Long userId) {

        if (userId == null) {
            throw new BusinessException(PARAMETER_NOT_FOUND);
        }

        UserBO userBO = BeanUtil.copyProperties(userService.getById(userId), UserBO.class);
        return ResultDTO.ok(userBO);
    }
}