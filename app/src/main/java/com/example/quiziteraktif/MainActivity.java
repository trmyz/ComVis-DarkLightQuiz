package com.example.quiziteraktif;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText inputNama;
    Button tombolMulai;
    SharedPreferences preferences;
    final String PREF_NAMA = "nama_pengguna";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputNama = findViewById(R.id.inputNama);
        tombolMulai = findViewById(R.id.btnMulai);

        preferences = getSharedPreferences("dataKuis", Context.MODE_PRIVATE);

        // Jika sudah ada nama tersimpan, langsung ke kuis
        if (preferences.contains(PREF_NAMA)) {
            startActivity(new Intent(MainActivity.this, QuizActivity.class));
            finish();
        }

        tombolMulai.setOnClickListener(v -> {
            String nama = inputNama.getText().toString().trim();
            if (nama.isEmpty()) {
                Toast.makeText(this, "Masukkan nama terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PREF_NAMA, nama);
            editor.putInt("indeks", 0);
            editor.putInt("skor", 0);
            editor.putInt("nyawa", 3);
            editor.apply();

            startActivity(new Intent(MainActivity.this, QuizActivity.class));
            finish();
        });
    }
}
