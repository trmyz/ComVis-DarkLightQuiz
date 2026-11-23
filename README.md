# Kuis Interaktif Berbasis Sensor Cahaya (Deteksi Gelap & Terang)

Aplikasi kuis interaktif yang memanfaatkan **sensor cahaya** pada perangkat untuk memvalidasi aksi pemain saat berpindah soal. Pada setiap soal, pemain diminta mengikuti instruksi pencahayaan tertentu—seperti **membuat kondisi gelap** (menutup area sensor cahaya/meredupkan ruangan) atau **membuat kondisi terang** (mengarahkan perangkat ke sumber cahaya)—sebelum dapat melanjutkan ke soal berikutnya.

---

## Fitur Utama

* **Kuis Interaktif Multi-Soal:** Menyajikan pertanyaan, pilihan jawaban, serta instruksi aksi pencahayaan pada tiap tahap soal.
* **Validasi Perpindahan Soal via Sensor Cahaya:** Pemain baru bisa berpindah ke soal berikutnya setelah sensor cahaya membaca kondisi **Gelap** atau **Terang** sesuai perintah aktif.
* **Pembacaan Intensitas Cahaya *Real-Time*:** Memantau nilai intensitas cahaya dari sensor perangkat (misalnya dalam satuan *lux*) secara langsung dan membandingkannya dengan ambang batas (*threshold*) yang ditentukan.
* **Indikator Status Sensor:** Menampilkan status pencahayaan yang terbaca saat ini agar pemain mengetahui apakah kondisi gelap/terang sudah terpenuhi.

---

## Alur Kerja & Deteksi Sensor Cahaya

1. Aplikasi mengaktifkan pembacaan **sensor cahaya** (*Light Sensor* / *Ambient Light Sensor*) saat sesi kuis dimulai.
2. Pemain membaca soal, menjawab pertanyaan, lalu mengikuti instruksi kondisi cahaya yang diminta (misalnya **Gelap** atau **Terang**).
3. Sistem membandingkan nilai intensitas cahaya yang diterima sensor dengan nilai ambang batas (*threshold*):
   * **Perintah GELAP:** Terpenuhi saat nilai intensitas cahaya turun di bawah batas gelap (*dark threshold*).
   * **Perintah TERANG:** Terpenuhi saat nilai intensitas cahaya naik melebihi batas terang (*bright threshold*).
4. Setelah kondisi pencahayaan sesuai perintah terdeteksi, kunci perpindahan soal terbuka dan pemain dapat melanjutkan ke soal berikutnya.

---

## Teknologi yang Digunakan

* **Platform / Framework:** *Android Studio / Java *
* **Sensor Perangkat:** Ambient Light Sensor (`Sensor.TYPE_LIGHT` pada Android atau *Sensor API* )
* **Version Control:** Git & GitHub
