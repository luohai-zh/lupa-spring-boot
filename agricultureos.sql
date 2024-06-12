/*
 Navicat Premium Data Transfer

 Source Server         : luohai
 Source Server Type    : MySQL
 Source Server Version : 80034
 Source Host           : localhost:3306
 Source Schema         : agricultureos

 Target Server Type    : MySQL
 Target Server Version : 80034
 File Encoding         : 65001

 Date: 27/04/2024 23:39:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for lupa_device
-- ----------------------------
DROP TABLE IF EXISTS `lupa_device`;
CREATE TABLE `lupa_device`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `userid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `deviceid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备ID',
  `deviceidsecretkey` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备秘钥',
  `devicename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备名称',
  `deviceremarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注信息',
  `createtime` int(0) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`, `deviceid`, `deviceidsecretkey`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '设备信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lupa_device
-- ----------------------------
INSERT INTO `lupa_device` VALUES (6, '3de43d788cd84f23a93d7ed0865067cd', 'g809bg', '461e02d8bdeff61ce014a2c7cc19ae27', '温湿度传感器', '测量环境的温度和湿度', 1713150741);

-- ----------------------------
-- Table structure for lupa_historydata
-- ----------------------------
DROP TABLE IF EXISTS `lupa_historydata`;
CREATE TABLE `lupa_historydata`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `deviceid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备ID',
  `paramsName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参数名',
  `paramsValues` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '参数值',
  `acceptTime` int(0) NULL DEFAULT NULL COMMENT '接收时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '历史数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lupa_historydata
-- ----------------------------
INSERT INTO `lupa_historydata` VALUES (25, 'g809bg', 'temp', '8', 1713243911);
INSERT INTO `lupa_historydata` VALUES (26, 'g809bg', 'humi', '7888', 1713243911);
INSERT INTO `lupa_historydata` VALUES (27, 'g809bg', 'temp', '8', 1713243916);
INSERT INTO `lupa_historydata` VALUES (28, 'g809bg', 'humi', '7888', 1713243916);
INSERT INTO `lupa_historydata` VALUES (29, 'g809bg', 'temp', '8', 1713243961);
INSERT INTO `lupa_historydata` VALUES (30, 'g809bg', 'humi', '7888', 1713243961);
INSERT INTO `lupa_historydata` VALUES (31, 'g809bg', 'temp', '8', 1713243962);
INSERT INTO `lupa_historydata` VALUES (32, 'g809bg', 'humi', '7888', 1713243962);
INSERT INTO `lupa_historydata` VALUES (33, 'g809bg', 'temp', '8', 1713243962);
INSERT INTO `lupa_historydata` VALUES (34, 'g809bg', 'humi', '7888', 1713243962);
INSERT INTO `lupa_historydata` VALUES (35, 'g809bg', 'temp', '8', 1713243962);
INSERT INTO `lupa_historydata` VALUES (36, 'g809bg', 'humi', '7888', 1713243962);
INSERT INTO `lupa_historydata` VALUES (37, 'g809bg', 'temp', '8', 1713243962);
INSERT INTO `lupa_historydata` VALUES (38, 'g809bg', 'humi', '7888', 1713243962);
INSERT INTO `lupa_historydata` VALUES (39, 'g809bg', 'temp', '8', 1713243962);
INSERT INTO `lupa_historydata` VALUES (40, 'g809bg', 'humi', '7888', 1713243962);
INSERT INTO `lupa_historydata` VALUES (41, 'g809bg', 'temp', '8', 1713243963);
INSERT INTO `lupa_historydata` VALUES (42, 'g809bg', 'humi', '7888', 1713243963);
INSERT INTO `lupa_historydata` VALUES (43, 'g809bg', 'temp', '8', 1713243963);
INSERT INTO `lupa_historydata` VALUES (44, 'g809bg', 'humi', '7888', 1713243963);
INSERT INTO `lupa_historydata` VALUES (45, 'g809bg', 'temp', '8', 1713243963);
INSERT INTO `lupa_historydata` VALUES (46, 'g809bg', 'humi', '7888', 1713243963);
INSERT INTO `lupa_historydata` VALUES (47, 'g809bg', 'temp', '8', 1713243963);
INSERT INTO `lupa_historydata` VALUES (48, 'g809bg', 'humi', '7888', 1713243963);

-- ----------------------------
-- Table structure for lupa_newdata
-- ----------------------------
DROP TABLE IF EXISTS `lupa_newdata`;
CREATE TABLE `lupa_newdata`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `deviceid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备ID',
  `paramsName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参数名',
  `paramsValues` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参数值',
  `acceptTime` int(0) NOT NULL COMMENT '接收时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '实时数据表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lupa_newdata
-- ----------------------------
INSERT INTO `lupa_newdata` VALUES (47, 'g809bg', 'temp', '8', 1713243963);
INSERT INTO `lupa_newdata` VALUES (48, 'g809bg', 'humi', '7888', 1713243963);

-- ----------------------------
-- Table structure for lupa_speak
-- ----------------------------
DROP TABLE IF EXISTS `lupa_speak`;
CREATE TABLE `lupa_speak`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `userid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `userspeak` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户消息',
  `createtime` int(0) NOT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户消息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lupa_speak
-- ----------------------------
INSERT INTO `lupa_speak` VALUES (16, '3de43d788cd84f23a93d7ed0865067cd', 'luohai', 'heoll world', 1713281161);
INSERT INTO `lupa_speak` VALUES (17, '3de43d788cd84f23a93d7ed0865067cd', 'luohai', '欢迎来到智农云', 1714136728);
INSERT INTO `lupa_speak` VALUES (18, '3de43d788cd84f23a93d7ed0865067cd', 'luohai', '详细使用教程请联系站长', 1714136769);
INSERT INTO `lupa_speak` VALUES (19, '3de43d788cd84f23a93d7ed0865067cd', 'luohai', 'luohai084@163.com', 1714136791);

-- ----------------------------
-- Table structure for lupa_user
-- ----------------------------
DROP TABLE IF EXISTS `lupa_user`;
CREATE TABLE `lupa_user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `userid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `userpassword` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户密码',
  `userphonenumber` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户手机号',
  `createtime` int(0) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`, `userid`, `userphonenumber`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lupa_user
-- ----------------------------
INSERT INTO `lupa_user` VALUES (20, '3de43d788cd84f23a93d7ed0865067cd', 'luohai', '670b14728ad9902aecba32e22fa4f6bd', '15277900654', 1713146810);

SET FOREIGN_KEY_CHECKS = 1;
