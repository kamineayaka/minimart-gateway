# minimart-gateway

MiniMart 的 API 网关进程。领域用语与 v1 契约在编排仓 [`minimart-infra`](../minimart-infra)。

- Spring 名：`gateway`
- 端口：8080
- 无业务库
- 公开路由：`/member/**` `/product/**` `/order/**` `/payment/**` → `lb://` 对应服务（StripPrefix=1）
- `/internal/**` 与 `/<service>/internal/**` 不转发
- 关联 ID：`X-Correlation-Id`（缺则生成，经 Feign 传到下游）

本机运行（Nacos 需已起，`NACOS_ADDR=127.0.0.1:8848`；无 Nacos 时 `optional:` 仍可起）：

```bash
./gradlew bootRun
```

编排：与其它仓并列 clone 后，在 `minimart-infra` 执行 `docker compose up`。Compose 使用 profile `runtime`（Nacos 配置必填）。构建需要 infra named context：`docker build --build-context infra=../minimart-infra .`
