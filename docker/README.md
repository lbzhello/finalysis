# ubuntu (暂未使用)
docker build -t lbz/ubuntu .
docker run --rm -it lbz/ubuntu /bin/bash

# postgres

进入当前 docker 目录 

- 构建/更新镜像
docker build -t lbz/postgres postgres/

- 启动容器
docker-compose up -d

- 进入容器 
docker exec -it postgres /bin/bash

- 运行命令 (注意会删库：只有初次可以执行！)
# docker run postgres /bin/echo "Hello world"
script/init_db.sh