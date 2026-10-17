# Ücretsiz AI Sağlayıcıları ve API Anahtarları

Bu belge, ATLAS projesinde kullanılabilecek ücretsiz (kredi kartı gerektirmeyen) AI sağlayıcılarını ve özelliklerini listeler.

## 1. Aktif Olanlar (Kurulu)
- **Gemini (Google AI Studio)**
  - Limit: Günde 1500 istek
  - Env: `ATLAS_AI_GEMINI_API_KEY`
  - Sağlayıcı Sınıfı: `GeminiProvider`
- **Groq**
  - Limit: Günde 14400 istek (Aşırı hızlı)
  - Env: `ATLAS_AI_GROQ_API_KEY`
  - Sağlayıcı Sınıfı: `OpenAICompatibleProvider`

## 2. Ücretsiz Anahtar Alınabilecek Diğer Sağlayıcılar
*(Çoğu OpenAI formatını kullandığı için kodda `OpenAICompatibleProvider` ile çalışır)*

### Cerebras
- **Özellik:** Dünyanın en hızlı çipleri (Groq benzeri). Günde 1 Milyon token ücretsiz.
- **Endpoint:** `https://api.cerebras.ai/v1/chat/completions`
- **Env:** `ATLAS_AI_CEREBRAS_API_KEY`
- **Kayıt:** cloud.cerebras.ai

### Mistral AI
- **Özellik:** Kalıcı "Free Tier" (ücretsiz katman) sunar.
- **Endpoint:** `https://api.mistral.ai/v1/chat/completions`
- **Env:** `ATLAS_AI_MISTRAL_API_KEY`
- **Kayıt:** console.mistral.ai

### OpenRouter
- **Özellik:** Tek API ile birçok modele erişim. Ücretsiz modeller (`:free` uzantılı) sürekli değişir. ATLAS'ın otomatik model keşfi sayesinde güncel ücretsiz modeli kendi bulur.
- **Endpoint:** `https://openrouter.ai/api/v1/chat/completions`
- **Env:** `ATLAS_AI_OPENROUTER_API_KEY`
- **Kayıt:** openrouter.ai/keys

### Hugging Face
- **Özellik:** Ücretsiz Inference API.
- **Endpoint:** `https://api-inference.huggingface.co/v1/chat/completions`
- **Env:** `ATLAS_AI_HUGGINGFACE_API_KEY`
- **Kayıt:** huggingface.co/settings/tokens

### Cohere
- **Özellik:** Aylık 1000 ücretsiz istek (Trial Key).
- **Env:** `ATLAS_AI_COHERE_API_KEY`
- **Not:** OpenAI formatını kullanmaz, kendi özel API'si vardır. Entegrasyon için `OpenAICompatibleProvider` yerine özel bir sınıf (`CohereProvider`) yazılması gerekir. *(Şimdilik öncelik değil).*

## 3. Ücretli Olanlar (Para Kazanınca Eklenecek)
- **OpenAI** (GPT-4 vb.)
- **Anthropic** (Claude)
- **DeepSeek** (Kendi API'sinde ücretsiz katman yok)
- **Together AI** (Bazen 5$ kredi verir ama sonrası ücretli)

## 4. Anahtar Ekleme Formatı
API anahtarları `~/.config/atlas/env` dosyasına şu formatta eklenir:
`export ATLAS_AI_SAGLAYICI_ADI_API_KEY=buraya_anahtar`
Sonrasında `source ~/.config/atlas/env` komutu çalıştırılır.
