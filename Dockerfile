# ステージ1: ビルド（Mavenを使用してjarを作成）
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# テストをスキップしてビルドを高速化
RUN mvn clean package -DskipTests

# ステージ2: 実行（軽量なJREイメージを使用）
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# ビルドステージから作成されたjarファイルをコピー
COPY --from=build /app/target/*.jar app.jar
# ポート8080を開放
EXPOSE 8080
# アプリを起動
ENTRYPOINT ["java", "-jar", "app.jar"]