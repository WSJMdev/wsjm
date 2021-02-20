package com.project.wsjm.mapper;

import java.util.List;

import com.project.wsjm.domain.UserDto;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper {

    List<UserDto> userList();
    UserDto fetchUserByID(int id);
    void updateUser(UserDto user);
    void insertUser(UserDto user);
    void deleteUser(int id);
}

