package com.example.quiziteraktif;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity implements SensorEventListener {

    // UI Components
    TextView teksSoal, teksNama, teksNyawa, teksInstruksi;
    RadioGroup pilihanJawaban;
    Button tombolJawab;

    // Quiz Data
    List<Soal> daftarSoal;
    int indeksSoal = 0;
    int skor = 0;
    int nyawa = 3;

    // Sensor Components
    SensorManager sensorManager;
    Sensor sensorCahaya;
    boolean instruksiTerpenuhi = false;
    boolean kuisSelesai = false;

    // User Data
    String namaPengguna;
    SharedPreferences preferences;

    // Media Players
    MediaPlayer mediaBenar, mediaSalah, mediaBgm, mediaSuksesAls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Initialize UI Components
        initializeUI();

        // Initialize User Data
        initializeUserData();

        // Initialize Sensor
        initializeSensor();

        // Initialize Media Players
        initializeMediaPlayers();

        // Initialize Quiz Data
        initializeQuizData();

        // Setup Event Listeners
        setupEventListeners();

        // Start Quiz
        tampilkanSoal();
    }

    /**
     * Initialize all UI components
     */
    private void initializeUI() {
        teksSoal = findViewById(R.id.teksSoal);
        teksNama = findViewById(R.id.teksNama);
        teksNyawa = findViewById(R.id.teksNyawa);
        teksInstruksi = findViewById(R.id.teksInstruksi);
        pilihanJawaban = findViewById(R.id.pilihanJawaban);
        tombolJawab = findViewById(R.id.tombolJawab);
    }

    /**
     * Initialize user data and preferences
     */
    private void initializeUserData() {
        preferences = getSharedPreferences("dataKuis", Context.MODE_PRIVATE);
        namaPengguna = preferences.getString("nama_pengguna", "");

        if (namaPengguna.isEmpty()) {
            mintaNamaPengguna();
            return;
        }

        // Load saved data
        indeksSoal = preferences.getInt("indeks", 0);
        skor = preferences.getInt("skor", 0);
        nyawa = preferences.getInt("nyawa", 3);

        // Update UI
        teksNama.setText("Nama: " + namaPengguna);
        updateTampilanNyawa();
    }

    /**
     * Initialize light sensor
     */
    private void initializeSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorCahaya = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (sensorCahaya == null) {
            Toast.makeText(this, "Sensor cahaya tidak tersedia!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Initialize all media players
     */
    private void initializeMediaPlayers() {
        try {
            mediaBenar = MediaPlayer.create(this, R.raw.benar);
            mediaSalah = MediaPlayer.create(this, R.raw.salah);
            mediaBgm = MediaPlayer.create(this, R.raw.music);
            mediaSuksesAls = MediaPlayer.create(this, R.raw.sukses);

            if (mediaBgm != null) {
                mediaBgm.setLooping(true);
                mediaBgm.start();
            }
        } catch (Exception e) {
            Log.e("MediaPlayer", "Error initializing media players: " + e.getMessage());
        }
    }

    /**
     * Initialize quiz questions and answers
     */
    private void initializeQuizData() {
        daftarSoal = new ArrayList<>();
        daftarSoal.add(new Soal(
                "Matahari terbit dari sebelah mana?",
                new String[]{"Timur", "Tenggara", "Barat", "Barat Laut"},
                "Timur",
                "Silakan jawab"
        ));
        daftarSoal.add(new Soal(
                "Pada siang hari Rani mematikan lampu teras karena sudah ada?",
                new String[]{"Cahaya Bulan", "Lampu Jalan", "Cahaya Matahari", "Senter"},
                "Cahaya Matahari",
                "Pergi ke tempat terang terlebih dahulu!"
        ));
        daftarSoal.add(new Soal(
                "Kapan waktu yang paling gelap?",
                new String[]{"Pagi", "Siang", "Sore", "Malam"},
                "Malam",
                "Pergilah ke tempat gelap dulu!"
        ));
        daftarSoal.add(new Soal(
                "Apa sumber cahaya alami?",
                new String[]{"Lilin", "Senter", "Matahari", "Lampu"},
                "Matahari",
                "Dekati sumber cahaya terang dulu!"
        ));
        daftarSoal.add(new Soal(
                "Apa yang digunakan untuk melihat dalam gelap?",
                new String[]{"Senter", "Kipas", "Speaker", "Kamera"},
                "Senter",
                "Masuk ke tempat gelap sebelum lanjut!"
        ));
    }

    /**
     * Setup event listeners
     */
    private void setupEventListeners() {
        tombolJawab.setOnClickListener(v -> cekJawaban());
    }

    /**
     * Update lives display with heart emojis
     */
    void updateTampilanNyawa() {
        StringBuilder nyawaEmoji = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            nyawaEmoji.append((i < nyawa) ? "❤️" : "🤍");
        }
        teksNyawa.setText(nyawaEmoji.toString());
    }

    /**
     * Show dialog to get user name
     */
    void mintaNamaPengguna() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Masukkan Nama Anda");

        final EditText inputText = new EditText(this);
        inputText.setHint("Nama Anda");
        builder.setView(inputText);
        builder.setCancelable(false);

        builder.setPositiveButton("OK", (dialog, which) -> {
            namaPengguna = inputText.getText().toString().trim();
            if (!namaPengguna.isEmpty()) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("nama_pengguna", namaPengguna);
                editor.putInt("indeks", 0);
                editor.putInt("skor", 0);
                editor.putInt("nyawa", 3);
                editor.apply();
                recreate();
            } else {
                mintaNamaPengguna();
            }
        });

        builder.show();
    }

    /**
     * Create custom styled RadioButton
     */
    private void createCustomRadioButton(String text, RadioGroup parent) {
        RadioButton rb = new RadioButton(this);
        rb.setText(text);

        // Apply custom layout params
        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dpToPx(12));
        rb.setLayoutParams(params);

        // Apply custom styling
        rb.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        rb.setTextSize(16);
        rb.setTextColor(ContextCompat.getColor(this, R.color.primary_text));
        rb.setBackground(ContextCompat.getDrawable(this, R.drawable.radio_button_custom));
        rb.setButtonDrawable(null); // Remove default radio button
        rb.setGravity(Gravity.CENTER_VERTICAL);
        rb.setElevation(dpToPx(2));

        // Add touch animation
        rb.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                    break;
            }
            return false;
        });

        parent.addView(rb);
    }

    /**
     * Convert dp to pixels
     */
    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    /**
     * Display current question
     */
    void tampilkanSoal() {
        pilihanJawaban.clearCheck();

        // Check if quiz is finished
        if (indeksSoal >= daftarSoal.size()) {
            akhiriKuis();
            return;
        }

        Soal soal = daftarSoal.get(indeksSoal);
        teksSoal.setText(soal.pertanyaan);
        teksInstruksi.setText(soal.instruksi);

        // Clear and add new radio buttons
        pilihanJawaban.removeAllViews();
        for (String pilihan : soal.pilihan) {
            createCustomRadioButton(pilihan, pilihanJawaban);
        }

        // Hide UI elements until instruction is fulfilled
        instruksiTerpenuhi = false;
        pilihanJawaban.setVisibility(View.GONE);
        tombolJawab.setVisibility(View.GONE);

        // Start listening to light sensor
        if (sensorCahaya != null) {
            sensorManager.registerListener(this, sensorCahaya, SensorManager.SENSOR_DELAY_UI);
        }
    }

    /**
     * Check the selected answer
     */
    void cekJawaban() {
        int id = pilihanJawaban.getCheckedRadioButtonId();
        if (id == -1) {
            Toast.makeText(this, "Pilih jawaban terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton dipilih = findViewById(id);
        String jawabanDipilih = dipilih.getText().toString();
        String jawabanBenar = daftarSoal.get(indeksSoal).jawaban;

        if (jawabanDipilih.equals(jawabanBenar)) {
            skor += 20;
            playSound(mediaBenar);
            showFeedback("Benar! +20 poin", true);
        } else {
            nyawa--;
            updateTampilanNyawa();
            playSound(mediaSalah);
            showFeedback("Salah! Jawaban: " + jawabanBenar, false);
        }

        // Check if game over
        if (nyawa <= 0) {
            akhiriKuis();
            return;
        }

        // Move to next question
        indeksSoal++;
        simpanData();

        // Delay before showing next question
        new android.os.Handler().postDelayed(() -> tampilkanSoal(), 1500);
    }

    /**
     * Show feedback message
     */
    private void showFeedback(String message, boolean isCorrect) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Play sound safely
     */
    private void playSound(MediaPlayer mediaPlayer) {
        try {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } catch (Exception e) {
            Log.e("MediaPlayer", "Error playing sound: " + e.getMessage());
        }
    }

    /**
     * Save current game data
     */
    void simpanData() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("indeks", indeksSoal);
        editor.putInt("skor", skor);
        editor.putInt("nyawa", nyawa);
        editor.apply();
    }

    /**
     * End the quiz and show results
     */
    void akhiriKuis() {
        kuisSelesai = true;

        // Stop sensor listening
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }

        // Stop background music
        if (mediaBgm != null && mediaBgm.isPlaying()) {
            mediaBgm.stop();
        }

        // Clear saved data
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Show results
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("nama", namaPengguna);
        intent.putExtra("skor", skor);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (kuisSelesai || indeksSoal >= daftarSoal.size()) return;

        float lux = event.values[0];
        Log.d("SensorCahaya", "Lux: " + lux);

        Soal soal = daftarSoal.get(indeksSoal);
        String instruksi = soal.instruksi.toLowerCase();
        instruksiTerpenuhi = false;

        // Check light conditions based on instruction
        if (instruksi.contains("gelap")) {
            if (lux < 5) {
                instruksiTerpenuhi = true;
            } else {
                teksInstruksi.setText("🌙 Terlalu terang! Cari tempat lebih gelap.");
            }
        } else if (instruksi.contains("terang")) {
            if (lux > 100) {
                instruksiTerpenuhi = true;
            } else {
                teksInstruksi.setText("☀️ Terlalu gelap! Cari tempat lebih terang.");
            }
        } else {
            instruksiTerpenuhi = true;
        }

        // Show UI elements when instruction is fulfilled
        if (instruksiTerpenuhi) {
            sensorManager.unregisterListener(this);
            teksInstruksi.setText("✅ Silakan jawab pertanyaan!");
            pilihanJawaban.setVisibility(View.VISIBLE);
            tombolJawab.setVisibility(View.VISIBLE);
            playSound(mediaSuksesAls);
        } else {
            pilihanJawaban.setVisibility(View.GONE);
            tombolJawab.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!instruksiTerpenuhi && !kuisSelesai && sensorCahaya != null) {
            sensorManager.registerListener(this, sensorCahaya, SensorManager.SENSOR_DELAY_UI);
        }
        if (mediaBgm != null && kuisSelesai) {
            mediaBgm.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
        // Pause music if playing
        if (mediaBgm != null && mediaBgm.isPlaying()) {
            mediaBgm.pause();
            kuisSelesai= true;
        } else {
            kuisSelesai = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Release all media players
        releaseMediaPlayer(mediaBgm);
        releaseMediaPlayer(mediaBenar);
        releaseMediaPlayer(mediaSalah);
        releaseMediaPlayer(mediaSuksesAls);
    }

    /**
     * Safely release media player
     */
    private void releaseMediaPlayer(MediaPlayer mediaPlayer) {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            }
        } catch (Exception e) {
            Log.e("MediaPlayer", "Error releasing media player: " + e.getMessage());
        }
    }

    /**
     * Question data class
     */
    class Soal {
        String pertanyaan;
        String[] pilihan;
        String jawaban;
        String instruksi;

        Soal(String pertanyaan, String[] pilihan, String jawaban, String instruksi) {
            this.pertanyaan = pertanyaan;
            this.pilihan = pilihan;
            this.jawaban = jawaban;
            this.instruksi = instruksi;
        }
    }
}