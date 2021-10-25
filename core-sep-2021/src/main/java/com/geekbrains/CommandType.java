package com.geekbrains;

public enum CommandType {
    FILE_MESSAGE,  //файл
    FILE_REQUEST,  //дай мне файл с именем
    LIST_REQUEST,  //дай мне список
    LIST_RESPONSE, //список
    PATH_IN_REQUEST,  //сервер перейди в директорию
    PATH_UP_REQUEST,  //сервер перейди вверх
    PATH_RESPONSE  //в какой директории сейчас сервер
}

