# Payment Service (Quarkus + Kafka + PostgreSQL)

Layanan contoh yang mengonsumsi event pembayaran dari Kafka, melakukan manipulasi/enrichment sederhana, menyimpan status ke PostgreSQL, dan menulis kembali event ke topik Kafka lain.

## Stack
- Java 17, Quarkus 3
- Kafka (Confluent images) + Zookeeper (dev only)
- PostgreSQL 15
- Flyway untuk migrasi DB
- Reactive Messaging (SmallRye) untuk Kafka

## Topik Kafka
- `payment.transactions` (incoming)
- `payment.transactions.enriched` (outgoing)

## Menjalankan secara lokal

### 1) Jalankan infrastruktur (Kafka + Postgres)
```bash
docker compose up -d
```

Verifikasi:
- Kafka broker pada `localhost:29092`
- Postgres pada `localhost:5432` (user: `postgres`, pass: `password`, db: `paymentdb`)

### 2) Jalankan aplikasi (dev mode)
```bash
mvn quarkus:dev
```

Aplikasi akan:
- Menjalankan migrasi Flyway (membuat tabel `payments`)
- Menjadwalkan generator event tiap 10 detik ke topik `payment.transactions`
- Mengonsumsi `payment.transactions`, normalisasi data, simpan ke DB, dan publish ke `payment.transactions.enriched`

### 3) Cek REST endpoint
```bash
curl "http://localhost:5632/payments?merchantId=mrc_1&limit=20"
```

## Konfigurasi
Lihat `src/main/resources/application.properties` untuk konfigurasi datasource dan Kafka.

## Catatan
- Jika topik tidak terbentuk otomatis (tergantung konfigurasi broker), buat manual:
```bash
# contoh (memerlukan util kafka, sesuaikan path container)
docker exec -it $(docker ps -q -f name=kafka) kafka-topics --create --topic payment.transactions --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

docker exec -it $(docker ps -q -f name=kafka) kafka-topics --create --topic payment.transactions.enriched --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```
