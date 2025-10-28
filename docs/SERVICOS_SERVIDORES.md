# Serviços e Servidores - Projeto Ticktzy

Documento completo de todos os serviços externos, servidores, APIs e bibliotecas de terceiros utilizados no projeto.

---

## 🌐 APIs e Backends Externos

### 1. **Back4App (Backend Parse)**

**URL**: `https://parseapi.back4app.com/`

**Descrição**: Backend BaaS (Backend as a Service) baseado em Parse Server para armazenamento e sincronização de dados.

**Uso no projeto**: Armazenamento e recuperação de produtos

**Configuração**:
- Headers adicionados via interceptor OkHttp (`ParseApiInterceptor`)
- Valores lidos de `local.properties` (ex.: `api.parseAppId`, `api.parseApiKey`)
- Exemplo: ver `local.properties.example`

**Endpoints utilizados**:
- `GET /classes/products` - Busca produtos por CNPJ
- `POST /classes/products` - Cria/atualiza produto

**Localização no código**:
- `app/src/main/java/br/com/sttsoft/ticktzy/repository/remote/MSCallProducts.kt`
- `app/src/main/java/br/com/sttsoft/ticktzy/repository/remote/repositorys/ProductsRepository.kt`
- `app/src/main/java/br/com/sttsoft/ticktzy/domain/GetProductsUseCase.kt`

**Timeout configurado**:
- Connect: 15 segundos
- Read: 10 segundos
- Write: 5 segundos

**Características**:
- Suporta queries com filtros
- Autenticação via headers
- Retorna JSON

---

### 2. **Delta Pag (API Principal)**

**URL**: `https://pos.deltapag.com.br/`

**Descrição**: API principal para operações de POS e pagamentos.

**Uso no projeto**: Busca informações do terminal, configurações SiTef e dados de pagamento

**Configuração**:
- Authorization Bearer adicionado via interceptor OkHttp (`AuthorizationInterceptor`)
- Token lido de `local.properties` (chave `api.bearerToken`)
- Exemplo: ver `local.properties.example`

**Endpoints utilizados**:
- `POST /pos/init` - Inicialização do terminal (busca infos)

**Localização no código**:
- Cliente: `app/src/main/java/br/com/sttsoft/ticktzy/repository/remote/MSCall.kt`
- Interceptores: `app/src/main/java/br/com/sttsoft/ticktzy/repository/remote/AuthorizationInterceptor.kt`
- Interface: `app/src/main/java/br/com/sttsoft/ticktzy/repository/remote/repositorys/InfoRepository.kt`
- Caso de uso: `app/src/main/java/br/com/sttsoft/ticktzy/domain/GetInfosUseCase.kt`

**Timeout configurado**:
- Connect: 15 segundos
- Read: 10 segundos
- Write: 5 segundos

**Características**:
- Autenticação via Bearer Token
- JSON request/response
- Suporta retry automático

---

## 💳 Integrações de Pagamento

### 3. **SiTef (Sistema Integrado de Transferência Eletrônica de Fundos)**

**Descrição**: Sistema integrado para processamento de pagamentos com cartão de crédito/débito e PIX.

**Activity**: `br.com.softwareexpress.sitef.msitef.ACTIVITY_CLISITEF`

**Uso no projeto**: Processamento de todos os pagamentos eletrônicos

**URLs e Endpoints**:
- **Endpoint TLS Production**: `tls-prod.fiservapp.com:443`
- **Endpoint Público**: Configurado dinamicamente via API Delta

**Funcionalidades implementadas**:

#### 3.1. **Teste de Conexão** (Modalidade 111)
```kotlin
fun testConnection(infos: InfoResponse): Intent
```
- Localização: `app/src/main/java/br/com/sttsoft/ticktzy/domain/SitefUseCase.kt:55`
- Propósito: Testar conectividade com servidor SiTef
- Parâmetros: data, hora, modalidade "111"

#### 3.2. **Pagamento** (Modalidades: 2, 3, 122)
```kotlin
fun payment(infos: InfoResponse, valor: Double, modalidade: String, isPix: Boolean, isTLSEnabled: Boolean): Intent
```
- Modalidade "2": Débito
- Modalidade "3": Crédito
- Modalidade "122": PIX
- Localização: `app/src/main/java/br/com/sttsoft/ticktzy/domain/SitefUseCase.kt:97`
- Usado em: `app/src/main/java/br/com/sttsoft/ticktzy/presentation/sale/ui/SaleActivity.kt`

#### 3.3. **Configuração de Token** (Modalidade 699)
```kotlin
fun tokenConfig(infos: InfoResponse): Intent
```
- Localização: `app/src/main/java/br/com/sttsoft/ticktzy/domain/SitefUseCase.kt:73`
- Propósito: Configuração de tokenização

#### 3.4. **Rastreamento** (Modalidade 121)
```kotlin
fun trace(infos: InfoResponse, isTLSEnabled: Boolean): Intent
```
- Localização: `app/src/main/java/br/com/sttsoft/ticktzy/domain/SitefUseCase.kt:119`

#### 3.5. **Cancelamento** (Modalidade 200)
```kotlin
fun cancelation(infos: InfoResponse, isTLSEnabled: Boolean): Intent
```
- Localização: `app/src/main/java/br/com/sttsoft/ticktzy/domain/SitefUseCase.kt:140`

#### 3.6. **Reimpressão** (Modalidade 112)
```kotlin
fun reprint(infos: InfoResponse, isTLSEnabled: Boolean): Intent
```
- Localização: `app/src/main/java/br/com/sttsoft/ticktzy/domain/SitefUseCase.kt:161`

#### 3.7. **Acesso Direto** (Modalidade 110)
```kotlin
fun directAccess(infos: InfoResponse, isTLSEnabled: Boolean): Intent
```
- Localização: `app/src/main/java/br/com/sttsoft/ticktzy/domain/SitefUseCase.kt:181`

**Parâmetros Comuns**:
- `operador`: "mSiTef"
- `timeoutColeta`: "30" segundos
- `enderecoSitef`: IP:Porta do servidor
- `empresaSitef`: Código da loja
- `cnpj_automacao`: CNPJ do estabelecimento
- `CNPJ_CPF`: CNPJ/CPF
- `dadosSubAdqui`: Dados da subaquirente
- `comExterna`: "1" (TLS) ou "0" (TCP)

**Suporte TLS**:
- Ativado via `VerifyTlsComunicationUseCase`
- URL TLS: `tls-prod.fiservapp.com:443`
- Configuração: `app/src/main/java/br/com/sttsoft/ticktzy/domain/VerifyTlsComunicationUseCase.kt`

---

## 🖨️ Impressoras e Hardware

### 4. **Impressoras Sunmi**

**Biblioteca**: `com.sunmi:printerlibrary:1.0.18`

**Uso no projeto**: Impressão de tickets, cupons e comprovantes

**Features**:
- Impressão térmica
- Códigos de barras
- QR Codes
- Texto formatado

**Localização no código**:
- `app/src/main/java/br/com/sttsoft/ticktzy/core/device/PrinterControllerSunmi.kt`
- `app/src/main/java/br/com/sttsoft/ticktzy/core/device/PrinterController.kt`
- `app/src/main/java/br/com/sttsoft/ticktzy/domain/PrinterUseCase.kt`
- `app/src/main/java/br/com/sttsoft/ticktzy/presentation/base/BaseActivity.kt`

**Métodos implementados**:
- `printInfo()` - Informações gerais
- `ticketPrint()` - Impressão de tickets
- `moneyReceiptPrint()` - Comprovante de dinheiro
- `printFinish()` - Relatório de fechamento

**Binding automático**:
- Via `BaseActivity.enablePrinterBinding`
- Conecta via `bindPrinter()`

---

## 📚 Bibliotecas e Dependências

### 6. **Android Jetpack Libraries**

#### 6.1. **Core KTX**
- **Versão**: `1.15.0`
- **Grupo**: `androidx.core:core-ktx`
- **Uso**: Extensions Kotlin para Core Android

#### 6.2. **AppCompat**
- **Versão**: `1.7.1`
- **Grupo**: `androidx.appcompat:appcompat`
- **Uso**: Compatibilidade com versões antigas do Android

#### 6.3. **Lifecycle**
- **Versões**: `2.8.4`
- **Módulos**:
  - `androidx.lifecycle:lifecycle-viewmodel-ktx`
  - `androidx.lifecycle:lifecycle-runtime-ktx`
- **Uso**: MVVM, gerenciamento de ciclo de vida

#### 6.4. **Activity/Fragment KTX**
- **Versões**: 
  - Activity: `1.9.1`
  - Fragment: `1.8.2`
- **Módulos**:
  - `androidx.activity:activity-ktx`
  - `androidx.fragment:fragment-ktx`
- **Uso**: Extensions para Activities e Fragments

#### 6.5. **Material Components**
- **Versão**: `1.12.0`
- **Grupo**: `com.google.android.material:material`
- **Uso**: Componentes de UI Material Design

---

### 7. **Networking**

#### 7.1. **Retrofit**
- **Versão**: `3.0.0`
- **Grupo**: `com.squareup.retrofit2:retrofit`
- **Uso**: Cliente HTTP REST

#### 7.2. **Retrofit Gson Converter**
- **Versão**: `3.0.0`
- **Grupo**: `com.squareup.retrofit2:converter-gson`
- **Uso**: Serialização JSON

#### 7.3. **OkHttp Logging Interceptor**
- **Versão**: `4.12.0`
- **Grupo**: `com.squareup.okhttp3:logging-interceptor`
- **Uso**: Logging de requisições HTTP
- **Nível de log**: BODY (debug) / NONE (release)

#### 7.4. **Gson**
- **Versão**: `2.13.1`
- **Grupo**: `com.google.code.gson:gson`
- **Uso**: Serialização/desserialização JSON

---

## 🔐 Autenticação e Segurança

### 9. **Tokens e API Keys**

Agora externalizados via `local.properties` e injetados por interceptors:

| Serviço | Como é injetado | Chaves em local.properties |
|---------|------------------|----------------------------|
| Back4App | `ParseApiInterceptor` | `api.parseAppId`, `api.parseApiKey` |
| Delta Pag | `AuthorizationInterceptor` | `api.bearerToken` |

---

## 🔧 Configuração por Ambiente

- Arquivo de exemplo: `local.properties.example`
- Arquivo real (não versionado): `local.properties`
- Chaves relevantes:
  - `useAPI`, `urlDelta`, `urlAPI`
  - `api.bearerToken`, `api.parseAppId`, `api.parseApiKey`
  - `keystore.file`, `keystore.storePassword`, `keystore.keyAlias`, `keystore.keyPassword`

---

## 🔧 Ferramentas de Build

### 10. **Gradle e Kotlin**

| Ferramenta | Versão | Descrição |
|-----------|--------|-----------|
| **Android Gradle Plugin** | 8.6.1 | Plugin de build Android |
| **Kotlin** | 2.1.21 | Linguagem de programação |
| **Gradle** | 8.7 | Sistema de build |

**Configurações**:
- Compile SDK: 35
- Min SDK: 24
- Target SDK: 34
- Java Toolchain: 21

---

## 📦 Armazenamento Local

### 11. **SharedPreferences**

**Uso**: Armazenamento local de dados (cache, configurações, estatísticas)

**Principais chaves**:

#### Estatísticas de Venda
- `SALES_MADE` - Total de vendas realizadas
- `CHARGE_MADE` - Total de cobranças
- `SANGRIA_MADE` - Total de sangrias
- `REINFORCE_MADE` - Total de reforços
- `CANCELS_MADE` - Total de cancelamentos

#### Por Tipo de Pagamento
- `DEBIT_TYPE` / `DEBIT_VALUE` - Débito
- `CREDIT_TYPE` / `CREDIT_VALUE` - Crédito
- `PIX_TYPE` / `PIX_VALUE` - PIX
- `MONEY_TYPE` / `MONEY_VALUE` - Dinheiro

#### Controle de Caixa
- `CAIXA` - Valor atual do caixa
- `CAIXA_INICIAL` - Valor inicial do caixa
- `CAIXA_ABERTO` - Status do caixa (true/false)
- `CAIXA_SANGRIA` - Total de sangria
- `CAIXA_REINFORCE` - Total de reforço

#### Configurações
- `SITEF_INFOS` - Informações do SiTef (InfoResponse)
- `TLS_ENABLED` - Status de TLS
- `senhaDoApp` - Senha do aplicativo

**Localização**: `app/src/main/java/br/com/sttsoft/ticktzy/core/prefs/`

---

## 🌍 URLs de Produção

| Ambiente | Serviço | URL |
|----------|---------|-----|
| **Produção** | Delta Pag | `https://pos.deltapag.com.br/` |
| **Produção** | Back4App | `https://parseapi.back4app.com/` |
| **TLS** | SiTef TLS | `tls-prod.fiservapp.com:443` |

**Configuração**: `app/build.gradle.kts` (linhas 46-47, 52-54)

```kotlin
buildConfigField("Boolean", "useAPI", "true")
buildConfigField("String", "urlDelta", "\"https://pos.deltapag.com.br/\"")
buildConfigField("String", "urlAPI", "\"https://parseapi.back4app.com/\"")
```

---

## 🔄 Fluxo de Integração

### Fluxo de Pagamento Eletrônico

```
SaleActivity
    ↓
Escolha forma de pagamento (PIX/Cartão)
    ↓
SitefUseCase.payment()
    ↓
Cria Intent com parâmetros SiTef
    ↓
Activity SiTef processa pagamento
    ↓
Retorna resultado em SaleActivity
    ↓
Imprime comprovante (POSMP/Sunmi)
    ↓
Atualiza estatísticas (SharedPreferences)
```

### Fluxo de Sincronização de Produtos

```
SplashActivity
    ↓
GetInfosUseCase → Delta Pag API
    ↓
Recebe InfoResponse
    ↓
GetProductsUseCase → Back4App API
    ↓
ProductSyncUseCase
    ↓
Salva em SharedPreferences
```

---

## 📊 Resumo de Dependências

### Dependências de Produção
- AndroidX Core KTX
- AndroidX AppCompat
- Material Components
- Lifecycle Components (ViewModel, Runtime)
- Activity/Fragment KTX
- Retrofit + Gson Converter
- OkHttp Logging Interceptor
- Lottie
- Coil
- Sunmi Printer Library
- POSMP API (via AAR)

### Dependências de Teste
- JUnit 4.13.2
- AndroidX JUnit 1.2.1
- AndroidX Espresso Core 3.6.1

---

## 🔒 Considerações de Segurança

1. **Tokens hardcoded**: As chaves de API estão no código - considerar externalização
2. **TLS**: Suporte opcional para comunicação segura
3. **Network Security**: Configurar Network Security Config para produção
4. **ProGuard**: Desabilitado atualmente - considerar habilitar para release
5. **Certificados**: Verificar validação de certificados SSL/TLS

---

## 📝 Checklist de Manutenção

- [ ] Rotacionar tokens de API regularmente
- [ ] Monitorar uso de API quotas
- [ ] Atualizar bibliotecas de terceiros
- [ ] Verificar compatibilidade com novos modelos de impressoras
- [ ] Testar integrações SiTef antes de releases
- [ ] Backup de dados em SharedPreferences
- [ ] Implementar Network Security Config
- [ ] Habilitar ProGuard para release

---

**Última atualização**: Janeiro 2025  
**Versão do app**: 1.0.11 (versionCode: 13)

