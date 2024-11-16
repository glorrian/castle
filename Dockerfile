FROM container-registry.oracle.com/graalvm/native-image:21-ol8 AS builder

RUN microdnf install findutils

WORKDIR /build

COPY . /build

RUN ./gradlew nativeBuild

FROM container-registry.oracle.com/os/oraclelinux:8-slim

COPY --from=builder build/build/native/nativeCompile castle-application
ENTRYPOINT ["/castle-application"]