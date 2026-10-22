# ATLAS — Gelişim Tarih Kayıtları

> Bu dosya ATLAS projesinin kronolojik gelişim kayıtlarını içerir.
> Tarihler, commit timestamp'leri ve oturum loglarından derlenmiştir.
> Bazı tarihler yaklaşıktır; kesin tarih için commit hash'ine bakınız.

---

## 2026-09-26-22:00 — İlk Altyapı
**Commit:** Öncesi (proje başlangıcı)
- AtlasUpdateManager.java oluşturuldu (APK installer)
- res/xml/atlas_file_paths.xml (FileProvider)
- docs/collaboration.md (AI ekip rolleri)
- AndroidManifest.xml: REQUEST_INSTALL_PACKAGES + FileProvider

---

## 2026-09-28-01:55 — CommandDispatcher Arşivleme
**Commit:** Öncesi
- CommandDispatcher-before-quote-parser.java archive/ altına alındı
- Esen kurala uygun: çalışan kod silinmez, arşivlenir

---

## 2026-10-02-22:00 — Failure Classification (PHASE 8)
**Commit:** 5876a17
**Dosyalar:**
- ImprovementFailureCategory.java (8 kategori)
- ImprovementFailureClassification.java
- ImprovementFailureClassifier.java (17 regex pattern)
- ImprovementFailureClassifierTest.java (16 test)

**Başarı:**
- 3 haftadır kırık olan E2E retry testi ilk kez geçti
- @Ignore'lar geri alındı (Handoff §20)
- Environment-aware early exit eklendi

---

## 2026-10-03-00:30 — Java-Only Fallback
**Commit:** b3864da
**Dosyalar:**
- ImprovementLabValidationMode.java (FULL / JAVA_ONLY)
- ImprovementLabBuildResult.validationMode alanı
- ImprovementLabEvaluationResult.partial alanı
- ImprovementLabBuildExecutor fallback (AAPT2 fail → compileDebugJavaWithJavac)
- ImprovementLabEvaluator: [PARTIAL] tanıma
- ImprovementSelfDevelopmentLoop: isPartial() erken çıkış

**Neden:**
AAPT2 Termux'ta nested build'de çalışmıyor. Dürüst PARTIAL validation eklendi.

---

## 2026-10-04-00:17 — Stuck Detection İskelet
**Commit:** 1edc4ea (sonraki gün)
- ImprovementFailureSignature.java
- ImprovementStuckDetector.java

---

## 2026-10-08-00:23 — Stuck Detection Loop Entegrasyonu
**Commit:** 1edc4ea
- Loop'a stuckDetector field eklendi
- previousSignature takibi
- Aynı hata 2 kez → [STUCK] erken çıkış

---

## 2026-10-10-00:18 — Termux Test Fix
**Commit:** 13abf5d
- app/build.gradle: forkEvery=1, maxParallelForks=1
- Test worker çökmeleri azaltıldı (signal 36, EOFException)

---

## 2026-10-12-22:00 — Safety Güçlendirme
**Commit:** 2faf42e + cdb86dd + 1689da2
**Dosyalar:**
- ImprovementChangeSafety: isSensitiveFile() + 5+ dosya kontrolü
- ImprovementChangeSafetyTest: 6 yeni test (12 toplam)
- ImprovementChangeProposalGenerator: calculateRisk() + calculateReversible()

**Neden:**
ProposalGenerator her şeye MEDIUM/reversible=true diyordu, Safety etkisizdi.

---

## 2026-10-14-00:35 — Project Memory İskelet
**Commit:** 6b1f6f6
- docs/project-state.md
- docs/history/events.jsonl (append-only format)
- İlk olay: STATE_INIT

---

## 2026-10-15-23:00 — AI Provider Genişlemesi (Başlangıç)
**Commit:** 2617e23
- OpenAICompatibleProvider.java (tek sınıf, çoklu sağlayıcı)
- Groq, OpenRouter, Cerebras bootstrap'a eklendi
- app/build.gradle: env değişkenleri test worker'a geçirildi

---

## 2026-10-16-21:39 — Groq Canlı Test
**Commit:** 9c6a36d
- GroqLiveTest.java
- Model: openai/gpt-oss-120b
- Gerçek API çağrısı başarılı: "Merhaba ATLAS"
- İlk ücretsiz yedek AI sağlayıcı aktif

---

## 2026-10-17-00:00 — Otomatik Model Keşfi
**Commit:** 06cf90e
- resolveModel() (config → /v1/models → cache)
- fetchAvailableModels(), chooseModel(), deriveModelsUrl()
- MODEL_PRIORITY listesi (gpt-oss, qwen, llama, mixtral)
- isAvailable() override (endpoint + key yeterli, model keşfedilir)

**Neden:**
Groq model adı değişti (llama-3.3 kaldırıldı). Otomatik uyum sağlandı.

---

## 2026-10-17-01:00 — 4 Yeni Sağlayıcı
**Commit:** 1b02abd
- Mistral (api.mistral.ai)
- HuggingFace (api-inference.huggingface.co)
- Together AI (api.together.xyz)
- DeepSeek (api.deepseek.com)

**Toplam:** 11 sağlayıcı destekleniyor

---

## 2026-10-18-00:15 — Snapshot ve Handoff
**Snapshot:** atlas_codebase.txt (517K), atlas_project.zip (191K)
- docs/ATLAS_SESSION_HANDOFF.md
- docs/free-ai-providers.md
- docs/chatgpt-collaboration-history.md
- SD karta kopyalandı

---

## 2026-10-22-01:15 — Duplicate Docs Temizliği
**Commit:** ff99994
- Aynı içerikli 3 Markdown dosyasından 2'si silindi
- Sadece chatgpt-collaboration-history.md kaldı

---

## 2026-10-22-01:20 — AI Konsensüs Kararları
**Commit:** a1977f1
- docs/ATLAS_AI_SYNTHESIS_DECISIONS.md
- Grok, Gemini, ChatGPT ortak kararları
- P0 sırası: Approval → Rollback → Stuck Detection → Project Memory

---

## 2026-10-22-19:30 — Stuck Detection Düzeltmesi
**Commit:** 5bc6e64
- ImprovementStuckDetector: kategori bazlı limit (CODE=3, diğerleri=2)
- Fixer artık stuck check'ten ÖNCE çağrılıyor
- AI 1 deneme yerine 3 deneme hakkı kazanıyor

---

## 2026-10-22-19:45 — 3 Yeni Sınıf
**Commit:** f2f305b
- DevelopmentState.java (10 durumlu onay akışı enum)
- SafeJsonlReader.java (bozuk JSONL satırlarını atlar)
- ImprovementSafeRollback.java (sadece git stash, git clean YASAK)

---

## 2026-10-22-19:50 — .gitignore Temizliği
**Commit:** e700011
- atlas_codebase_*.txt ve atlas_project_*.zip ignore'a eklendi
- Snapshot'lar artık commit'e girmiyor

---

## 2026-10-22-19:58 — StuckDetector Testleri
**Commit:** 3bc4cca
- ImprovementStuckDetectorTest.java (6 test)
- CODE=3, ENVIRONMENT=2, TOOLCHAIN=2 doğrulandı
- Backward compatible overload test edildi

---

## 2026-10-22-20:00 — Snapshot (Tarihli)
**Dosyalar:**
- atlas_codebase_2026-10-22.txt (525K)
- atlas_project_2026-10-22.zip (214K)
- SD karta kopyalandı

---

## SIRADAKİ HEDEFLER (Yarın)

### P0 — Approval Mekanizması
- [ ] DevelopmentState'i ImprovementSelfDevelopmentLoop'a bağla
- [ ] Kullanıcı onayı nasıl alınacak? (callback / Service / UI)
- [ ] Grok'un önerisi: Diff göster + E/H/diff seçenekleri

### P0 — Rollback Entegrasyonu
- [ ] ImprovementSafeRollback'i LAB hata akışına yerleştir
- [ ] archiveFailedWorkspace ile koordine et
- [ ] Rollback başarısız olursa ne yapmalı?

### P1 — Project Memory Okuyucu
- [ ] ProjectMemory.java (SafeJsonlReader kullanır)
- [ ] Olay yazıcı (Loop/Runtime'dan)
- [ ] AI prompt'una geçmiş entegre etme

### P1 — AAPT2 Çözümü (PHASE 7)
- [ ] Wrapper script veya AGP downgrade dene
- [ ] Java-Only yeterli mi, tam build mi gerekli?

### P2 — İki Terminal UI
- [ ] DevTerminalActivity (kullanıcı onayı için)
- [ ] Rol tabanlı yetkilendirme

---

## KRİTİK KURALLAR (Hatırlatma)

- **git reset --hard YASAK** — kullanıcı verisini siler
- **git clean -fd YASAK** — stash'i siler
- **@Ignore YASAK** — testler gizlenmez
- **Kanıt → Minimum Değişiklik → Test**
- **BUILD SUCCESS ≠ FEATURE VERIFIED**
- **Ortam hatası ≠ kod hatası**
- **Rollback = Snapshot öncelik, git stash fallback**

---

## NOTLAR

- Tarihler `YYYY-MM-DD-HH` formatında
- Yaklaşık tarihler: commit timestamp'leri baz alındı
- Kesin tarih için ilgili commit hash'ini kontrol edin
- Bu dosya her oturum sonunda güncellenmeli
