<<<<<<< HEAD
# springboot-codeql
=======
# springbootCodeql

Quick start

1. Build and run (default port 8080):

```bash
./run.sh
```

2. Build and run on a different port:

```bash
./run.sh --server.port=8081
```

Common IDE fix

- Eclipse: Run → Run Configurations... → Java Application → Select your configuration → Main class: `com.springboot.springbootCodeql.SpringbootCodeqlApplication`. Ensure project classpath is present.
- IntelliJ: Run → Edit Configurations → Set Main class to `com.springboot.springbootCodeql.SpringbootCodeqlApplication`.

Troubleshooting

- If you see `java.lang.IllegalStateException: No sources defined`, you're launching `org.springframework.boot.SpringApplication` (or the spring-boot jar) instead of your application's main class. Use the commands above or fix your IDE run configuration.
- If you get database errors remove `spring-boot-starter-data-jpa` or add an appropriate datasource driver and configuration in `src/main/resources/application.properties`.
>>>>>>> 303fa86 (Add CodeQL workflow)
