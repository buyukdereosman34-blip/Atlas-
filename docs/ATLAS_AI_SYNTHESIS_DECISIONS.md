# ATLAS — AI Danışma Sentezi ve P0 Kararları
**Tarih:** 22 Ekim 2026
**Katılımcılar:** Grok, Gemini, ChatGPT (Sentez: Sistem Mimarı)

## 1. ORTAK KARARLAR VE VİZYON
- **AAPT2 / Cloud:** Telefon içinde (Termux) kalınacak. Cloud CI/CD (GitHub Actions) şimdilik gereksiz karmaşıklık. Java-Only + PARTIAL doğrulama ile devam edilecek. AAPT2 ayrı bir savaş (PHASE 7) olarak ertelenecek.
- **Multi-Agent (ATLAS City):** Grok'un "Blackboard + Message Queue" (Ortak Kara Tahta) yaklaşımı benimsendi. Ajanlar birbirinin çalışma alanını (working tree) ezemeyecek.
- **Local Model (Offline):** Gelecekte Qwen2.5-1.5B / Phi-3-mini gibi modellerle çalışacak. Önce Runtime ve Tool katmanı stabilize edilecek.

## 2. TEKNİK KARARLAR (DÜZELTİLMİŞ)
- **State Machine (Onay Akışı):** Gemini'nin sunduğu `Enum + State Design Pattern` hibrit yapısı kullanılacak. ChatGPT'nin belirlediği 10 durum (PROPOSED, PENDING_APPROVAL, APPROVED, APPLYING, VALIDATING, PROMOTED, ROLLBACK_REQUIRED, ROLLED_BACK, REJECTED, FAILED) uygulanacak.
- **Rollback (Geri Alma):** 
  - `git reset --hard` KESİNLİKLE KULLANILMAYACAK (kullanıcının commit edilmemiş kodlarını siler).
  - Öncelik: `ImprovementLabManager`'daki mevcut "Snapshot" (dosya kopyalama) altyapısının tamamlanması.
  - Alternatif: Eğer Git kullanılacaksa, Gemini'nin kodu DÜZELTİLEREK sadece `git stash --include-untracked` kullanılacak. (Gemini'nin kodundaki tehlikeli `git clean -fd` komutu kaldırıldı, çünkü stash'i siler).
- **Stuck Detection (Takılma):** Mevcut "2. hatada dur" kuralı ÇOK AGRESİF. ChatGPT'nin önerisiyle:
  - **CODE** hatalarında: AI'ya 3 düzeltme denemesi hakkı verilecek.
  - **ENVIRONMENT / PERMISSION** hatalarında: Anında durulacak.
- **JSONL Okuyucu:** Gemini'nin bozuk son satırı atlayan `SafeJsonlReader` kodu projeye eklenecek.
- **İki Terminal (UI):** Gemini'nin önerdiği `AtlasApplication` (Singleton) yapısı kullanılarak iki Activity'nin aynı Runtime'ı bellek sızıntısı olmadan paylaşması sağlanacak.

## 3. YARININ P0 (ÖNCELİKLİ) PLANI
1. `DevelopmentState` Enum'unun (10 durumlu) oluşturulması.
2. `SafeRollback` sınıfının yazılması (Snapshot öncelikli, Git fallback'li).
3. `StuckDetection` sınıfının ChatGPT'nin eşiklerine (CODE=3) göre güncellenmesi.
4. `SafeJsonlReader` sınıfının eklenmesi.
5. Testlerin yazılması ve yeşil olmasının doğrulanması.
