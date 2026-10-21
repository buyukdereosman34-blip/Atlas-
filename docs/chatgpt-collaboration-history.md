# ATLAS — ChatGPT ile Geliştirme Geçmişi ve Karar Notları

> **Belge türü:** Yalnızca dokümantasyon. ATLAS uygulamasının çalışma zamanına dahil değildir.
> **Amaç:** ChatGPT ile yapılan mimari değerlendirmeleri, hata ayıklama çalışmalarını, kullanıcı tercihlerini ve daha sonra ele alınacak özellikleri kaybetmemek.
> **Güvenlik kuralı:** Bu belge mevcut Java/Kotlin kaynaklarını, Gradle yapılandırmasını, Android Manifest'ini veya çalışan akışları değiştirmez. Kodda otomatik olarak okunmaz ya da çalıştırılmaz.

## 1. ATLAS'ın amacı
ATLAS; Android ve Termux üzerinde geliştirilen, tek bir AI sağlayıcısına bağımlı olmayan bir AI geliştirme ve çalışma zamanı platformudur. Hedef; Android uygulaması, terminal, araçlar, çalışma zamanı, AI çekirdeği, proje/kod motoru ve güvenli öz-geliştirme döngüsünü bir araya getirmektir.

## 2. Korunacak mimari ilkeler
**AI karar verir → Tool yeteneği tanımlar → Policy/Permission izinleri denetler → Runtime yürütür → Backend yürütme mekanizmasını sağlar → Android/Termux işlemi gerçekleştirir.**
Planlanan genel akış: `User → AI Agent → Intent/Task → Planner → Tool Selection → Policy/Permission → Runtime → Backend → Android/Termux → Observation → AI Evaluation`

## 3. ChatGPT'nin ATLAS geliştirme sürecindeki rolü
- **Grok:** Fikirler, alternatif mimariler ve araştırma.
- **ChatGPT:** Mantık ve mimari kontrolü, planlama, tutarlılık/çelişki incelemesi, hata teşhisi.
- **Gemini:** Kod üretimi, refactor ve uygulama ayrıntıları.
- **Kullanıcı / Radish:** Son karar, önceliklendirme, onay veya ret.

## 4. Geliştirme ve hata ayıklama kuralları
- **Kanıt → minimum değişiklik → test.**
- `BUILD SUCCESS != FEATURE VERIFIED`: Derlemenin başarılı olması özelliğin doğru çalıştığını tek başına kanıtlamaz.
- AAPT2/Gradle/SDK gibi ortam sorunları, kanıt olmadan uygulama kodu hatası diye sınıflandırılmaz.
- Test hataları gizlenmez; `@Ignore` ile başarısız testler başarı gibi gösterilmez.

## 5. Build / AAPT2 konusunda kayıtlı teşhis
- Temiz/fresh Gradle derlemesinde “AAPT2 bin Daemon #0: Daemon startup failed” hatası alınmıştır.
- Önerilen Java-only fallback, yalnızca uygun AAPT2/resource-toolchain hatasında kısmi doğrulama olarak kullanılmalıdır.
- Örnek doğru rapor: “Kısmi doğrulama: Java kaynakları derlendi. Android resource ve test doğrulaması AAPT2 toolchain sorunu nedeniyle yapılamadı.”

## 6. Self-Development / LAB için hedeflenen güvenli akış
`Analyze → Proposal → Safety/Policy → Isolated LAB → Apply → Build/Test → Evaluation → Promotion → Learning`
- LAB'de yapılan değişiklikler doğrulanmadan ana kaynaklara terfi ettirilmemelidir.

## 7. ATLAS özellik envanteri
- **Temel Mühendislik:** Android Uygulaması, Terminal, Tool System, Runtime, Loglar, Android Runtime, Project/Code Engine, Build/Test Engine, Hata Sınıflandırma, Self-Development LAB, Project Memory, Policy/Permission, Agent/Planner.
- **AI ve Ajanlar:** Provider-independent AI Core, Cloud/Local/Hybrid, Çoklu Ajan Rolleri.
- **Araştırma ve Güvenlik:** Web araştırması, APK/Manifest analizi, Açık kaynak incelemesi, Savunma amaçlı güvenlik taraması, Oyun geliştirme/mod.
- **Akıllı Oyun Performans Yöneticisi:** CPU/RAM/GPU/FPS izleme, profil seçimi, ayarları geri yükleme.
- **ATLAS City:** AI vatandaşları, iç krediler, gerçek gelir yönetimi, şehir başkanı (kullanıcı) onayı.

## 8. Askıda Bırakılan / Unutulmaması İstenen Fikirler
- Project Memory
- ChatGPT Copy + ATLAS Bridge + Command Starter
- Obsidian ATLAS Knowledge-Project Mirror
- GitHub Copilot / AI Agent entegrasyonu (Askıda)
- PRoot / RootFS alternatif çalışma zamanı
- Farklı AI mimarileriyle alternatif ATLAS varyantları
- ATLAS City ekonomisi
