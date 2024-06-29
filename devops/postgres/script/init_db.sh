#!/bin/bash
# 注意：此脚本会删库重建，只有初次运行时需要执行
# 数据库初始化脚本
psql -U postgres -c "drop database if exists stock;"
psql -U postgres -c "create database stock;"
psql -U postgres -d stock -f sql/init.sql
psql -U postgres -d stock -f sql/function.sql

