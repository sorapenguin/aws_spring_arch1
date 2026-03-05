# Exam Learning SaaS インフラ設計ポートフォリオ
## アーキテクチャ構成図

![Architecture Diagram](docs/architecture.png)

## 1. 概要

本プロジェクトは、資格学習を目的としたWebアプリケーションを想定した
インフラ設計ポートフォリオである。  

小規模ローンチ（同時アクセス100人規模）を前提に、
AWS Well-Architected Framework に基づいた設計を行う。

※ 本リポジトリはアーキテクチャ設計および構成検討を目的としており、
実装コードは最小限とする。

---

## 2. 想定ユーザー・規模

- 同時アクセス：100人
- 月間アクティブユーザー：1,000〜3,000人
- データ規模：問題データ 数万件
- 個人情報を扱う商用利用を想定

---

## 3. 機能要件

### ユーザー機能

- ユーザー登録／ログイン
- 問題一覧表示
- 回答送信・即時正誤判定
- 学習履歴閲覧

### 管理者機能

- 問題CRUD
- ユーザー管理

---

## 4. 非機能要件

### 4.1 可用性

- 目標SLA：99.9%
- Multi-AZ構成
- EC2 Auto Scaling（最小2台）
- RDS Multi-AZ構成

### 4.2 性能

- API応答：500ms以内
- 同時100人アクセスで性能劣化しない設計
- 水平スケール可能な構成

### 4.3 セキュリティ

- IAM最小権限設計
- ALBでHTTPS終端（ACM証明書利用）
- RDSはPrivate Subnet配置
- S3 Block Public Access有効化
- パスワードはハッシュ化保存
- セキュリティグループによる最小通信制御

### 4.4 拡張性

- Auto Scalingによるスケールアウト
- ECS / Fargate移行可能な構成
- CloudFront導入余地あり

### 4.5 バックアップ

- RDS自動バックアップ（7日）
- S3バージョニング有効化

---

## 5. AWS構成

- VPC（/16）
  - Public Subnet ×2（ALB）
  - Private Subnet ×2（Application）
  - Private Subnet ×2（RDS）
- Application Load Balancer
- EC2 Auto Scaling Group（min:2）
- RDS（Multi-AZ）
- S3（静的コンテンツ・ログ）
- CloudWatch Logs
- IAMロール分離
- NAT Gateway ×1（小規模前提）

---

## 6. 設計方針

- 小規模ローンチに対して過剰設計を避ける
- 単一障害点を可能な限り排除
- コストと可用性のバランスを重視
- 将来のスケールを見据えた拡張可能な設計

---

## 7. 単一障害点（SPOF）

- NAT Gatewayは単一構成（コスト優先）
- RDSはMulti-AZで冗長化
- ALBはマネージド冗長構成

---

## 8. 今後の改善案

- ECS / Fargate移行
- Aurora移行
- ElastiCache（Redis）導入
- AWS WAF導入
- CloudFront追加

---

## 設計思想

本設計は、小規模ローンチに適したコスト効率の高い構成としつつ、
将来的なスケールや可用性向上に対応できる拡張可能なアーキテクチャを採用している。

設計構築エンジニアとして、
ベストプラクティス・可用性・セキュリティ・拡張性のバランスを重視した構成とした。