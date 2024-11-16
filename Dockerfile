FROM ghcr.io/graalvm/native-image:ol8-23.0.1

WORKDIR /app

COPY . .

RUN ./gradlew nativeBuild

FROM alpine:latest
COPY --from=0 /app/build/native/nativeCompile/castle-application /app/castle-application
ENTRYPOINT ["/app/my-app"]