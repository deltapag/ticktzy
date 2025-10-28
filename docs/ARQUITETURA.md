# Arquitetura do Projeto Ticktzy

## 📋 Visão Geral

**Ticktzy** é um sistema de PDV (Ponto de Venda) desenvolvido em Kotlin para dispositivos Android, especificamente otimizado para impressoras Sunmi. O aplicativo gerencia vendas, caixa, integração com SiTef para pagamentos e sincronização de produtos com backend.

### Informações Técnicas
- **Package**: `br.com.sttsoft.ticktzy`
- **Versão**: 1.0.11 (versionCode: 13)
- **MinSDK**: 24
- **TargetSDK**: 34
- **CompileSDK**: 35
- **Linguagem**: Kotlin 2.1.21
- **AGP**: 8.6.1

---

## 🏗️ Estrutura de Arquitetura

O projeto segue uma arquitetura **MVVM (Model-View-ViewModel)** com padrões **Clean Architecture**, organizando o código em camadas distintas.

```
app/src/main/java/br/com/sttsoft/ticktzy/
├── core/              # Componentes compartilhados
├── domain/            # Lógica de negócio (Use Cases)
├── features/          # Features específicas (MVVM)
├── presentation/      # UI (Activities, Dialogs, Components)
├── repository/        # Camada de dados (API, Cache)
└── extensions/        # Extensions Kotlin
```

---

## 📂 Estrutura Detalhada por Camadas

### 1. **Core** (`core/`)
Camada de componentes fundamentais reutilizáveis.

#### **Device** (`core/device/`)
- **PosmpReceiptController**: Contrato para controle de impressão POSMP
- **PosmpReceiptControllerImpl**: Implementação do controller POSMP
- **PrinterController**: Contrato para controle de impressora
- **PrinterControllerSunmi**: Implementação específica para impressoras Sunmi

#### **Preferences** (`core/prefs/`)
- **PrefsGateway**: Interface para armazenamento local
- **PrefsGatewayImpl**: Implementação usando SharedPreferences

#### **Result** (`core/result/`)
- **AppResult**: Sealed class para resultados de operações
- **ErrorModel**: Modelo de erro estruturado
- **ErrorMapper**: Mapeamento de erros

#### **UI** (`core/ui/`)
- **BaseViewModel**: ViewModel base genérico com StateFlow
- **UiState**: Interface para estados da UI
- **UiEvent**: Interface para eventos da UI
- **UiEffect**: Interface para efeitos colaterais (Side Effects)
- **defaultVmFactory**: Factory para criação de ViewModels
- **viewbinding/collect.kt**: Extensions para ViewBinding

### 2. **Domain** (`domain/`)
Camada de **Use Cases** - Lógica de negócio isolada.

#### Use Cases Principais:
- **GetDadosSubUseCase**: Busca dados de subaquirente
- **GetInfosUseCase**: Busca informações do terminal
- **GetProductsUseCase**: Busca produtos do servidor
- **ProductCacheUseCase**: Gerenciamento de cache local de produtos
- **ProductSyncUseCase**: Sincronização de produtos (cache ↔ servidor)
- **PrinterUseCase**: Lógica de impressão
- **SitefUseCase**: Integração com SiTef (pagamentos)
- **TerminalnfosUseCase**: Informações do terminal
- **VerifyTlsComunicationUseCase**: Verificação de comunicação TLS

### 3. **Features** (`features/`)
Módulos de funcionalidades com MVVM completo.

#### **Sale** (`features/sale/`)
- **SaleState**: Estado da tela de vendas
- **SaleEvent**: Eventos da tela de vendas
- **SaleEffect**: Efeitos colaterais (navegação, toasts)
- **SaleViewModel**: ViewModel com lógica de vendas
- **ui/SalesActivity**: Activity de vendas

### 4. **Presentation** (`presentation/`)
Interface do usuário (UI).

#### **Base** (`presentation/base/`)
- **BaseActivity**: Activity base com funcionalidades compartilhadas
  - Binding de impressoras (Sunmi e POSMP)
  - Diálogos de loading
  - Controle de navegação
- **SplashActivity**: Tela inicial
  - Coleta informações do servidor
  - Sincroniza produtos
  - Define aplicativo home

#### **Home** (`presentation/home/`)
- **HomeActivity**: Tela principal com opções
  - Nova Venda
  - Cobrança
  - Configurações
  - Relatórios

#### **Sale** (`presentation/sale/`)
- **ui/SaleActivity**: Tela de vendas
- **components/**:
  - **PaymentBar**: Barra de pagamentos
  - **ProductAdapter**: Adapter de produtos
  - **SearchBar**: Barra de busca

#### **Cashier** (`presentation/cashier/`)
Gestão de caixa:
- **start/**: Abertura de caixa
- **home/**: Home do caixa
- **finish/**: Fechamento de caixa
- **reinforce/**: Reforço de caixa
- **sangria/**: Sangria

#### **Config** (`presentation/config/`)
- **ConfigActivity**: Configurações gerais
- **ConfigSitefInfosActivity**: Configuração SiTef
- **ConfigTicketActivity**: Configuração de tickets

#### **Dialogs** (`presentation/dialogs/`)
- **ChangeDialog**: Diálogo de troco
- **ConfirmDialog**: Diálogo de confirmação
- **InputDialog**: Diálogo de input
- **LoadingDialogFragment**: Loading
- **PaymentTypeChooseDialog**: Escolha de forma de pagamento

### 5. **Repository** (`repository/`)
Camada de dados.

#### **Local** (`repository/local/`)
- **product.kt**: Modelos de cache local (SharedPreferences)

#### **Remote** (`repository/remote/`)
- **MSCall**: Client Retrofit base
- **MSCallProducts**: Client para produtos
- **repositorys/**: Interfaces Retrofit
  - **InfoRepository**: Informações do terminal
  - **ProductsRepository**: Produtos
- **request/**: Modelos de requisição
  - **Terminal**: Dados do terminal
  - **TerminalWrapper**: Wrapper de terminal
- **response/**: Modelos de resposta
  - **ProductsResponse**: Resposta de produtos
  - **InfoResponse**: Informações do terminal
  - **Sitef**, **LojaSitef**, **Pagamento**, etc.

### 6. **Extensions** (`extensions/`)
Extensões Kotlin para facilitar operações:
- **DoubleExt**: Extensões para Double
- **ImageViewExt**: Extensões para ImageView
- **LongExt**: Extensões para Long
- **NetworkExt**: Extensões de rede
- **SharedPrefsExtensions**: Extensões para SharedPreferences
- **StringExt**: Extensões para String

---

## 🔄 Fluxo de Funcionamento

### 1. **Inicialização** (SplashActivity)
```
SplashActivity → Busca Infos → Sincroniza Produtos → Verifica Caixa → Navega
```

1. Carrega informações do terminal via `TerminalnfosUseCase`
2. Busca informações do servidor via `GetInfosUseCase`
3. Sincroniza produtos via `ProductSyncUseCase`
4. Verifica se caixa está aberto
5. Navega para `ActivityCashierStart` ou `HomeActivity`

### 2. **Vendas** (SaleViewModel)
```
Event → ViewModel → UseCase → Repository → Result → State Update → UI Effect
```

**Fluxo de Venda:**
1. Usuário adiciona produtos
2. Evento de pagamento (dinheiro, PIX, cartão)
3. Processamento via ViewModel
4. Integração com SiTef (pagamentos eletrônicos)
5. Impressão de comprovantes
6. Atualização de estatísticas

**Tipos de Pagamento:**
- **Dinheiro** (`CashPay`): Validação de valor recebido e troco
- **PIX** (`PixPay`): Integração com SiTef (modo 122)
- **Cartão** (`CardPay`): Débito (modo 2) ou Crédito (modo 3)

### 3. **Caixa**
- **Abertura**: `ActivityCashierStart`
- **Operações**: Reforço, Sangria
- **Fechamento**: `ActivityCashierFinish`
  - Cálculo de totais por tipo de pagamento
  - Impressão de relatórios
  - Exportação de dados

---

## 🔌 Integrações Externas

### 1. **Backend APIs**
- **URL Delta**: `https://pos.deltapag.com.br/`
- **URL Parse (Back4App)**: `https://parseapi.back4app.com/`
- **Authentication**: X-Parse-Application-Id e X-Parse-REST-API-Key

### 2. **SiTef (Pagamentos)**
Integração com SiTef para:
- Pagamentos com cartão (débito/crédito)
- Pagamentos via PIX
- Comunicação TLS
- Geração de comprovantes

### 3. **Impressoras**
- **Sunmi**: Biblioteca `com.sunmi:printerlibrary:1.0.18`
- **POSMP**: Biblioteca `.aar` em `app/libs/posmpapi_1.01.10-partnersRelease.aar`

---

## 📊 Padrões de Arquitetura

### **MVVM (Model-View-ViewModel)**
```kotlin
Activity (View) → ViewModel → UseCase → Repository → Data Source
```

### **Clean Architecture**
```
Presentation → Domain ← Repository
```

### **State Management**
- **StateFlow**: Estados reativos
- **Channel**: Efeitos colaterais
- **State**: Imutável, gerenciado por ViewModel

### **Dependency Injection**
- Manual (via construtores)
- Factory pattern em `defaultVmFactory`

---

## 🛠️ Stack Tecnológica

### **Android**
- ViewBinding
- DataBinding
- Lifecycle Components
- Navigation (implícito)

### **Coroutines & Flow**
- Coroutines para operações assíncronas
- StateFlow para estados reativos
- Channel para efeitos colaterais

### **Networking**
- **Retrofit**: `3.0.0`
- **OkHttp**: Logging Interceptor `4.12.0`
- **Gson**: Serialização `2.13.1`

### **UI**
- **Material Components**: `1.12.0`
- **Lottie**: Animações `6.6.7`
- **Coil**: Carregamento de imagens `2.7.0`

### **Utilities**
- Sunmi Printer Library
- POSMP API (nativa)

---

## 📱 Activities e Funcionalidades

| Activity | Descrição |
|----------|-----------|
| `SplashActivity` | Inicialização e sincronização |
| `HomeActivity` | Menu principal |
| `SaleActivity` | Tela de vendas |
| `ActivityCashierStart` | Abertura de caixa |
| `ActivityCashierHome` | Home do caixa |
| `ActivityCashierFinish` | Fechamento de caixa |
| `ConfigActivity` | Configurações |
| `ConfigSitefInfosActivity` | Config SiTef |
| `ChargeActivity` | Cobrança |
| `ActivitySitefHome` | Home SiTef |

---

## 🔐 Build Configuration

### **Debug**
- API habilitada
- URLs de produção configuradas
- TLS desabilitado

### **Release**
- API habilitada
- Minify desabilitado
- ProGuard configurado
- Signing configurado (comentado)

---

## 📝 Observações

1. **SharedPreferences**: Usado extensivamente para cache local
2. **TLS**: Suporte para comunicação segura com SiTef
3. **Cache de Produtos**: Sincronização bidirecional (servidor ↔ local)
4. **Impressão**: Suporte dual (Sunmi + POSMP)
5. **SiTef**: Integração completa para pagamentos eletrônicos
6. **Estatísticas**: Tracking de vendas por tipo de pagamento

---

## 🚀 Próximos Passos Sugeridos

1. Implementar Dependency Injection (Dagger/Hilt)
2. Adicionar mais testes unitários
3. Migrar para Navigation Component
4. Implementar room para cache de produtos
5. Adicionar analytics (Firebase)
6. Implementar atualização de firmware OTA
7. Adicionar suporte offline completo

