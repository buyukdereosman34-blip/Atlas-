# ATLAS — Mimari Kararlar

## 2026-10-22 — Approval ve Rollback Kararları

### 1. Diff Zorunlu (Grok)
Kullanıcı onayı (E/H) istenmeden önce, değişecek dosyaların Git Diff özeti gösterilecek.
- Sadece değişen satırlar + 2 satır bağlam.
- Yeşil (+), Kırmızı (-) renkler.
- 15 satırdan uzunsa [D] Tam Diff seçeneği.
- Kör onay (sadece E/H) KESİNLİKLE YASAK.

### 2. Rollback Sınırları (ChatGPT)
Her hata rollback gerektirmez. Ayrım şudur:
- **LAB Hatası (Hedefe dokunulmadı):** Sadece `archiveFailedWorkspace()` çağrılır, rollback yapılmaz.
- **Safety Hatası:** Değişiklik uygulanmaz, rollback yapılmaz.
- **Promotion Sırasında Kısmi Değişiklik:** Hedef projede değişiklik oluştuysa, Snapshot veya `git stash` (SADECE bu durumda) ile rollback yapılır.
- **Rollback Başarısız Olursa:** Durum `ROLLBACK_REQUIRED` kalır, kullanıcıya manuel müdahale uyarısı gider. `PROMOTED` veya `ROLLED_BACK` durumuna ASLA geçilmez.
