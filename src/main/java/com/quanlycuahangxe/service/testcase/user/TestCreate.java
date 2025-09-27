///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.quanlycuahangxe.service.testcase.user;
//
//import com.quanlycuahangxe.model.User;
//import com.quanlycuahangxe.service.impl.UserServiceImpl;
//import com.quanlycuahangxe.service.interfaces.UserService;
//import com.quanlycuahangxe.utils.ServiceResult;
//
///**
// *
// * @author gunnguyen
// */
//public class TestCreate {
//
//    private static final UserService userService = new UserServiceImpl();
//
//    public static void main(String[] args) {
//        testCreateUser();
//    }
//
//    private static void testCreateUser() {
//        System.err.println("-- test tạo user");
//        ServiceResult<User> result = userService.createUser(
//                "testuser123",
//                "pass12333",
//                "Nguyễn Văn B",
//                "test214@gmail.com",
//                "MANAGER",
//                "",
//                "0911222333"
//        );
//        if (result.isSuccess()) {
//            System.out.println("Thành công: " + result.getData());
//        } else {
//            System.out.println("Thất bại: " + result.getMessage());
//        }
//    }
//
//}
