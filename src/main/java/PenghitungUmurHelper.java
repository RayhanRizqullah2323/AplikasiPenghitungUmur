/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Lenovo
 */
import java.time.LocalDate;
import java.time.Period;

public class PenghitungUmurHelper {

    // Menghitung umur secara detail (tahun, bulan, hari)
    public String hitungUmurDetail(LocalDate lahir, LocalDate sekarang) {
        Period period = Period.between(lahir, sekarang);
        return period.getYears() + " tahun, " + period.getMonths() + " bulan, " + period.getDays() + " hari";
    }

    // Menghitung hari ulang tahun berikutnya
    public LocalDate hariUlangTahunBerikutnya(LocalDate lahir, LocalDate sekarang) {
        LocalDate ulangTahunBerikutnya = lahir.withYear(sekarang.getYear());
        if (!ulangTahunBerikutnya.isAfter(sekarang)) {
            ulangTahunBerikutnya = ulangTahunBerikutnya.plusYears(1);
        }
        return ulangTahunBerikutnya;
    }

    // Menerjemahkan teks hari ke bahasa Indonesia
    public String getDayOfWeekInIndonesian(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY:
                return "Senin";
            case TUESDAY:
                return "Selasa";
            case WEDNESDAY:
                return "Rabu";
            case THURSDAY:
                return "Kamis";
            case FRIDAY:
                return "Jumat";
            case SATURDAY:
                return "Sabtu";
            case SUNDAY:
                return "Minggu";
            default:
                return "";
        }
    }
    // Mendapatkan peristiwa penting secara baris per baris
public void getPeristiwaBarisPerBaris(LocalDate tanggal, JTextArea txtAreaPeristiwa, Supplier<Boolean> shouldStop) {
    try {
        // Periksa jika thread seharusnya dihentikan sebelum dimulai
        if (shouldStop.get()) {
            return;
        }

        // URL untuk mengambil data
        String urlString = String.format("https://byabbe.se/on-this-day/%d/%d/events.json",
                tanggal.getMonthValue(), tanggal.getDayOfMonth());
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Properti koneksi HTTP
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        // Periksa status respons
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP response code: " + responseCode +
                    ". Silakan coba lagi nanti atau cek koneksi internet.");
        }

        // Membaca data dari server
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder content = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                // Periksa jika thread seharusnya dihentikan saat membaca data
                if (shouldStop.get()) {
                    javax.swing.SwingUtilities.invokeLater(() ->
                            txtAreaPeristiwa.setText("Pengambilan data dibatalkan.\n"));
                    return;
                }
                content.append(inputLine);
            }

            // Proses JSON hasil
            JSONObject json = new JSONObject(content.toString());
            JSONArray events = json.getJSONArray("events");

            if (events.isEmpty()) {
                javax.swing.SwingUtilities.invokeLater(() ->
                        txtAreaPeristiwa.setText("Tidak ada peristiwa penting yang ditemukan pada tanggal ini."));
                return;
            }

            // Iterasi peristiwa dan tambahkan ke JTextArea
            for (int i = 0; i < events.length(); i++) {
                // Periksa jika thread seharusnya dihentikan sebelum memproses data
                if (shouldStop.get()) {
                    javax.swing.SwingUtilities.invokeLater(() ->
                            txtAreaPeristiwa.setText("Pengambilan data dibatalkan.\n"));
                    return;
                }

                JSONObject event = events.getJSONObject(i);
                String year = event.optString("year", "Tidak diketahui");
                String description = event.optString("description", "Deskripsi tidak tersedia");
                String peristiwa = String.format("%s: %s", year, description);

                javax.swing.SwingUtilities.invokeLater(() ->
                        txtAreaPeristiwa.append(peristiwa + "\n"));
            }
        } finally {
            conn.disconnect();
        }
    } catch (Exception e) {
        javax.swing.SwingUtilities.invokeLater(() ->
                txtAreaPeristiwa.setText("Gagal mendapatkan data peristiwa: " + e.getMessage()));
    }
}
