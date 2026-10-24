# ATLAS — Bekleyen İşler ve Fikirler (Backlog)

> **DİKKAT:** Bu dosyadaki hiçbir madde şu anki ana rotamızı (Self-Development) etkilemez.
> Ana rotamız dışındaki fikirler, UI tasarımları ve eksik entegrasyonlar burada saklanır.
> Self-Development tamamen bitene kadar bu listedeki hiçbir maddeye dokunulmayacaktır.

## 2026-10-23 — Kullanıcının Aklındaki Yan Fikirler

1. **UI / Görünüm Tasarımı:**
   - Kullanıcı mevcut bir görünümü sevmedi.
   - Grok'a yatırılacak.
   - İki Terminal (Kullanıcı ve Geliştirici) aynı şekilde tasarlanacak (muhtemelen ferah/Termux tarzı).

2. **Eksik Başlatıcılar (Starters):**
   - `pkg install` gibi terminal komutları veya dış uygulama başlatıcıları eksik. (Terminal arka planında çalışacak Runner kodu).

3. **İsmi Unutulan Uygulamanın Özü:**
   - 4. madde olarak eklenecekti. İsmi unutuldu (Yarın netleşecek - Muhtemelen Shizuku veya ChatGPT kopyası).

4. **Uzaktan Müdahale:**
   - 5. madde: Uzaktan müdahale / Remote Control entegrasyonu.

5. **ChatGPT Eklenen Maddeler:**
   - 6. madde: ChatGPT ile konuşurken eklenmesine karar verilen ancak şu an hatırlanmayan bir madde.

6. **Genel Eksikler:**
   - 7. madde: Projenin genelinde çok fazla eksik var (bunlar zaten `project-state.md` ve roadmap'te tutuluyor).

---
*Bu dosya, ana odağımız olan Self-Development (PHASE 12) tamamlanana kadar dondurulmuştur.*

## 2026-10-24 — Ekip Çalışması Sonrası Fikirler (Kaybolmasın!)

### 🔍 Perplexity'nin AAPT2 Çözümü:
- **Sorun:** AGP'nin indirdiği AAPT2 x86_64 ve Termux'un kendi AAPT2'si SDK 35'i desteklemiyor.
- **Çözüm:** `termux-android-studio` projesinden derlenmiş güncel native aarch64 AAPT2 kullanmak.
- **Gradle Ayarı:** `~/.gradle/gradle.properties` dosyasına `android.aapt2FromMavenOverride=/data/data/com.termux/files/home/android-sdk/build-tools/36.0.0/aapt2` eklenecek.
- **Release için:** `android.enableResourceOptimizations=false` eklenecek.

### 🎨 Gemini & ChatGPT'nin UI Kodları:
- **DevTerminalActivity.kt:** İki terminali (Kullanıcı ve Geliştirici) ayırmak için gereken yeni Activity iskeleti.
- **ApprovalDialog.kt:** Kullanıcıdan E/H onayı almak için `AlertDialog` iskeleti.
- **Not:** Bu kodlar UI (Arayüz) aşamasında kullanılacak, şimdilik bekliyor.

### 🛡️ Claude'un Güvenlik Uyarıları (Kapatıldı):
- `ImprovementLabBuildExecutor` içindeki env dump kaldırıldı (API key sızıntısı engellendi).
- `ImprovementSafeRollback` içine `GIT_DIR` temizliği ve `.git` dizin kontrolü eklendi.
- `ImprovementSelfDevelopmentLoop` içine `ApprovalCallback` ve `REJECTED` durumu eklendi.
