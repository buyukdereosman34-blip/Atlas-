# ATLAS PROJESİ — KAPSAMLI DURUM VE DEVİR-TESLİM BELGESİ

**Tarih:** 2026-10-18
**Son Commit:** (Snapshot sonrası atılacak)
**Mevcut Aşama:** PHASE 8 (Failure Classification) Tamamlandı, PHASE 12 (Self-Development) Devam Ediyor.

---

## 1. BUGÜNE KADAR YAPILANLAR (Geçmiş 4 Hafta)
- **Mimari Kurulum:** Android + Termux üzerinde çalışan, AI Provider bağımsız (Cloud/Local/Hybrid) bir sistemin temeli atıldı.
- **Tool & Runtime:** `AtlasRuntime`, `ToolRegistry`, `ShellCommandExecutor` ve `PolicyEngine` katmanları oluşturuldu.
- **Self-Development İskeleti:** LAB ortamı (izole çalışma alanı), Change Proposal, Promoter ve Evaluator sınıfları yazıldı.
- **AAPT2 Krizi:** Termux'ta nested (iç içe) Gradle çalışırken AAPT2 Daemon'ın çöktüğü tespit edildi. (Bu sorun hala devam ediyor, Java-only mod ile workaround üretildi).
- **@Ignore Baskısı:** Testler ortam sorunları yüzünden bir ara @Ignore ile gizlendi, ancak Handoff §20 gereği bu yasaklandı ve testler tekrar aktif edildi.

---

## 2. SON OTURUMDA YAPILANLAR (Bu Hafta)
- **Failure Classification (PHASE 8):** 8 kategori (CODE, TEST, BUILD, ENVIRONMENT, TOOLCHAIN, vb.) ve 17 regex pattern içeren deterministik hata sınıflandırıcı eklendi.
- **Environment-Aware Loop:** Hata ortam kaynaklıysa (AAPT2 gibi) döngü erken çıkıyor, boşuna 3 kere denemiyor.
- **Java-Only Fallback:** AAPT2 çökerse, sadece `compileDebugJavaWithJavac` çalıştırılarak PARTIAL doğrulama yapılıyor.
- **Stuck Detection:** Aynı hata 2 kez üst üste gelirse `[STUCK]` denilerek döngü erken kesiliyor.
- **Termux Test Fix:** `forkEvery = 1` ve `maxParallelForks = 1` eklenerek test worker çökmeleri (signal 36, EOF) minimize edildi.
- **Safety Güçlendirmesi:** Risk seviyeleri ve hassas dosya (AndroidManifest, build.gradle, improvement/) kontrolü eklendi. 12 test geçiyor.
- **Generator Risk Hesabı:** Risk artık dosya tipine göre otomatik hesaplanıyor.
- **AI Provider Genişlemesi:** 11 sağlayıcı destekleniyor. Groq, OpenRouter, Mistral, Cerebras vb. eklendi. Otomatik model keşfi (Auto-Discovery) eklendi.
- **Project Memory:** `docs/` klasörü ve `events.jsonl` ile olay kayıt sistemi başlatıldı.

---

## 3. MEVCUT DURUM VE SINIRLAR
- **Testler:** 168 test var. Ortam (AAPT2/Worker) sorunları yüzünden bazen flaky (kararsız) çalışıyor.
- **Aktif AI Sağlayıcıları:** Gemini (1500 istek/gün), Groq (14400 istek/gün).
- **AAPT2 Engeli:** Fresh (temiz) build hala başarısız. Sadece cache varsa veya Java-only modda çalışıyor.

---

## 4. SIRADAKİ İŞLER (Yarın ve Sonrası)
1. **Approval State Machine (#12):** Kullanıcı onay mekanizması (EVET/HAYIR/BEKLE).
2. **Rollback Point (#15):** Yanlış bir değişiklikte geri dönüş mekanizması.
3. **Project Memory Tamamlama:** Kararların ve geçmişin kalıcı olarak JSONL dosyasına yazılması.
4. **AAPT2 Çözümü (PHASE 7):** AAPT2'yi devre dışı bırakma veya AGP sürüm düşürme denemeleri.
5. **İki Terminal UI:** Biri normal kullanıcı, biri ATLAS geliştirme için.

---

## 5. ÖNEMLİ KURALLAR (HANDOFF PRENSİPLERİ)
- Kanıt gelmeden hipotez kurma (§34).
- Ortam problemi (AAPT2) ile kod problemi ayrımını koru (§21).
- @Ignore kullanma (§20).
- BUILD SUCCESS != FEATURE VERIFIED (§35).
- Çalışan Java kodunu sırf modern görünüyor diye Kotlin'e çevirme.
