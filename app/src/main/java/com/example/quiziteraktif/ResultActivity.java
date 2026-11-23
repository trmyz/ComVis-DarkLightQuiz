package com.example.quiziteraktif;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    TextView teksNama, teksSkor, teksPesan;
    Button btnUlangi, btnKeluar;

    MediaPlayer mediaGameOver;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        teksNama = findViewById(R.id.teksNamaHasil);
        teksSkor = findViewById(R.id.teksSkor);
        teksPesan = findViewById(R.id.teksPesan);
        btnUlangi = findViewById(R.id.btnUlangi);
        btnKeluar = findViewById(R.id.btnKeluar);

        String nama = getIntent().getStringExtra("nama");
        int skor = getIntent().getIntExtra("skor", 0);

        teksNama.setText("Nama: " + nama);
        teksSkor.setText("Skor Anda: " + skor);

        // Pemilihan pesan dan suara berdasarkan skor
        if (skor >= 80) {
            teksPesan.setText("Hebat! Kamu luar biasa!");
            mediaGameOver = MediaPlayer.create(this, R.raw.selamat);
        } else if (skor >= 40) {
            teksPesan.setText("Bagus! Tapi kamu bisa lebih baik!");
            mediaGameOver = MediaPlayer.create(this, R.raw.kalah);
        } else {
            teksPesan.setText("Coba lagi, jangan menyerah!");
            mediaGameOver = MediaPlayer.create(this, R.raw.kalah);
        }

        // Putar suara akhir
        if (mediaGameOver != null) {
            mediaGameOver.start();
        }

        // Tombol ulangi kuis
        btnUlangi.setOnClickListener(v -> {
            preferences = getSharedPreferences("dataKuis", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("indeks", 0);
            editor.putInt("skor", 0);
            editor.putInt("nyawa", 3);
            editor.apply();

            Intent intent = new Intent(this, QuizActivity.class);
            startActivity(intent);
            finish();
        });

        // Tombol keluar ke MainActivity dan hapus nama
        btnKeluar.setOnClickListener(v -> {
            preferences = getSharedPreferences("dataKuis", Context.MODE_PRIVATE);
            preferences.edit().clear().apply();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaGameOver != null) {
            mediaGameOver.stop();
            mediaGameOver.release();
        }
    }
}
