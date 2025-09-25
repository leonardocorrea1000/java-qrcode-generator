# QR Code Generator com Google Cloud Storage

Este projeto é um serviço em **Java Spring Boot** para geração e armazenamento de QR Codes em nuvem.  
Os arquivos são salvos no **Google Cloud Storage (GCS)**, permitindo escalabilidade, segurança e fácil acesso.

---

## 🚀 Funcionalidades
- Geração de QR Codes com identificadores únicos.
- Upload automático para o **Google Cloud Storage**.
- Retorno da URL pública do arquivo armazenado.
- Configuração simples via `application.yml`.

---

## 🛠️ Tecnologias Utilizadas
- **Java 21+**
- **Spring Boot 3+**
- **Google Cloud Storage SDK**
- **Maven** para gerenciamento de dependências

---

## 📦 Estrutura Principal
- `core/StorageInterface.java` → Interface de abstração para armazenamento.
- `infra/GoogleCloudStorageAdapter.java` → Implementação usando Google Cloud Storage.
- `application.yml` → Configurações do projeto (bucket, projectId, credenciais).

---

## ⚙️ Configuração

### 1. Criar um bucket no Google Cloud Storage
- Acesse o [Console do Google Cloud](https://console.cloud.google.com/).
- Crie um bucket e copie o nome.

### 2. Gerar credenciais
- Crie uma **Service Account** com permissão para acessar o GCS.
- Baixe o arquivo JSON de credenciais e salve no seu projeto/local seguro.

gcs:
  bucket-name: nome-do-seu-bucket
  project-id: id-do-seu-projeto
  credentials-path: caminho/para/credentials.json
