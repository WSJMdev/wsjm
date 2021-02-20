package com.project.wsjm.controller;

import java.util.List;

import com.project.wsjm.domain.UserDto;
import com.project.wsjm.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserMapper userMapper;

    @GetMapping
    public List<UserDto> userList(){
        System.out.println(userMapper.userList());
        System.out.println("유저리스트 출력 성공");
        return userMapper.userList();
    }

    @PostMapping
    void insertUser(@RequestBody UserDto user) {
        userMapper.insertUser(user);
        System.out.println("유저 DB 저장 성공");
        // 프론트에 헤더 코드 201번으로 보내주기
    }

    @GetMapping("/{id}")
    public UserDto fetchUserByID(@PathVariable int id) {
        System.out.println(userMapper.fetchUserByID(id));
        UserDto fetchUser = userMapper.fetchUserByID(id);
        return fetchUser;
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable int id, @RequestBody UserDto user) {

        UserDto updateUser = user;
        System.out.println("업데이트유저 => " + updateUser);

        updateUser.setLanguage(user.getLanguage());

        userMapper.updateUser(updateUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        userMapper.deleteUser(id);
        System.out.println("유저 삭제, 성공적");
    }

}
