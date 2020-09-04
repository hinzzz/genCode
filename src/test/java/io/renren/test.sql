CREATE TABLE product_info (
   product_id  bigint COMMENT '产品编号' NOT NULL,
   product_name varchar(128) COMMENT '产品名称' NOT NULL,
   status varchar(20) COMMENT '产品状态 0-未上架 1-已上架 2-已下架 3-已删除',
   create_time timestamp COMMENT '创建时间' NOT NULL,
   update_time timestamp COMMENT '最后更新时间' NOT NULL,
   PRIMARY KEY(activity_id) USING BTREE
)
COMMENT = '产品信息'
ENGINE = InnoDB
CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;