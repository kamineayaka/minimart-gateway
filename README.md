# minimart-gateway

MiniMart 的 API 网关进程。领域用语与 v1 契约在编排仓 [`minimart-infra`](../minimart-infra)。

- Spring 名：`gateway`
- 端口：8080
- 无业务库；骨架阶段没有业务路由

本机运行（Nacos 需已起，`NACOS_ADDR=127.0.0.1:8848`）：

```bash
./gradlew bootRun
```

编排：与其它仓并列 clone 后，在 `minimart-infra` 执行 `docker compose up`。
