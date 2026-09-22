# minimart-gateway

MiniMart 的 API 网关进程。领域用语与 v1 契约在编排仓 [`minimart-infra`](../minimart-infra)。

- Spring 名：`gateway`
- 端口：8080
- 无业务库
- 公开路由：`/member/**` `/product/**` `/order/**` `/payment/**` → `http://<service>:<port>`（StripPrefix=1）
- `/internal/**` 与 `/<service>/internal/**` 不转发
- 关联 ID：`X-Correlation-Id`（缺则生成，经 Feign 传到下游）

本机运行：

```bash
./gradlew bootRun
```

编排：与其它仓并列 clone 后，在 `minimart-infra` 执行 `docker compose up`（无 Nacos；Docker DNS 与 K8s Service 名一致）。长期部署见 infra `charts/minimart/`（P2 Helm）。构建需要 infra named context：`docker build --build-context infra=../minimart-infra .`
