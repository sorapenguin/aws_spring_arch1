# Ping-t 風 Web 問題演習アプリ

資格試験対策サービス「Ping-t」をモデルにした、問題演習 Web アプリのポートフォリオ作品です。
Spring Boot をバックエンドに、ユーザー認証・問題管理・習得レベル追跡・ランダム演習などの機能を実装しました。

---

## 🖥️ 技術スタック

| カテゴリ | 使用技術 |
|---|---|
| バックエンド | Java 17 / Spring Boot 3.5 |
| Web フレームワーク | Spring MVC / Spring Security |
| テンプレートエンジン | Thymeleaf |
| データアクセス | Spring Data JPA / Hibernate |
| データベース | H2 Database（インメモリ） |
| ビルドツール | Maven |
| コンテナ | Docker（マルチステージビルド） |
| その他 | Lombok / Spring Validation |

---

## 🚀 主な機能

### ユーザー管理
- ユーザー登録・ログイン・ログアウト
- BCrypt によるパスワードハッシュ化
- Spring Security によるロールベースのアクセス制御

### 問題演習
- 問題一覧の表示
- 1 問ずつの演習（解答 → 解説 → 次の問題）
- **ランダム演習モード**：習得レベルや出題数を指定して演習できる
- AJAX による非同期の解答判定

### 習得レベル管理
ユーザーごとに問題の習得状況を 4 段階で記録します。

| レベル | 意味 |
|---|---|
| -1 | 直近で不正解 |
| 0 | 未出題 |
| 1 | 1 回正解（合格リーチ） |
| 2 | 2 回連続正解（習得済み） |

### ダッシュボード（トップページ）
- 全問題の総数、習得済み件数、合格リーチ件数をリアルタイム集計
- ログインユーザーごとに個人の進捗を表示

---

## 📂 ディレクトリ構成

```
src/main/java/com/example/pingt/
├── config/
│   ├── SecurityConfig.java       # Spring Security 設定
│   ├── DataInitializer.java      # 初期データ投入
│   └── DataLoader.java
├── domain/
│   ├── Question.java             # 問題エンティティ
│   ├── QuestionChoice.java       # 選択肢 Enum
│   ├── QuestionStatus.java       # ユーザー別習得状況
│   ├── PracticeRecord.java       # 解答履歴
│   └── User.java                 # ユーザーエンティティ
├── repository/
│   ├── QuestionRepository.java
│   ├── QuestionStatusRepository.java
│   ├── PracticeRecordRepository.java
│   └── UserRepository.java
├── service/
│   ├── QuestionService.java      # 問題取得・ランダム抽出ロジック
│   ├── PracticeService.java
│   ├── PracticeRecordService.java # 解答記録・習得レベル更新
│   ├── ProfileService.java
│   └── UserService.java
└── web/
    ├── HomeController.java        # トップページ（進捗集計）
    ├── PracticeController.java    # 演習・解答判定（AJAX）
    ├── RandomPracticeController.java
    ├── QuestionController.java
    ├── UserController.java
    ├── ProfileController.java
    └── LoginController.java
```

---

## ⚙️ セットアップ・起動方法

### Docker を使う場合（推奨）

```bash
# イメージのビルド
docker build -t pingt-app .

# コンテナの起動
docker run -p 8080:8080 pingt-app
```

### Maven を使う場合

```bash
# ビルド
mvn clean package -DskipTests

# 起動
java -jar target/*.jar
```

起動後、`http://localhost:8080` にアクセスしてください。

### H2 コンソール（DB 確認）

`http://localhost:8080/h2-console` にアクセスし、以下の情報でログインできます。

| 項目 | 値 |
|---|---|
| JDBC URL | `jdbc:h2:mem:testdb` |
| ユーザー名 | `sa` |
| パスワード | （空欄） |

---

## 🔑 設計のポイント

**セッションベースのランダム演習**
出題する問題 ID リストをサーバーサイドのセッション（`HttpSession`）に保持し、1 問ずつ取り出して表示する設計を採用しました。これにより画面遷移をまたいで問題の順番を保持できます。

**ユーザーごとの習得レベル管理**
`QuestionStatus` エンティティで `User` と `Question` を ManyToOne で紐付けることで、複数ユーザーが同じ問題を独立して解答・管理できる構造にしました。

**非同期解答判定**
解答送信は AJAX（`fetch` API）で `POST /practice/{id}/answer` にリクエストを送り、`@ResponseBody` で JSON を返すことで、ページリロードなしに正誤フィードバックを表示します。

**マルチステージ Docker ビルド**
Dockerfile をビルドステージ（Maven）と実行ステージ（JRE のみ）に分離し、最終イメージのサイズを最小化しています。

---

## 📝 今後の改善点（TODO）

- [ ] ランダム演習での習得レベルによるフィルタリングを完全実装（現在は全問からシャッフル）
- [ ] H2 から PostgreSQL / MySQL への切り替え対応
- [ ] テストコードの追加（JUnit5 / Mockito）
- [ ] AWS へのデプロイ（EC2 + RDS 構成）
