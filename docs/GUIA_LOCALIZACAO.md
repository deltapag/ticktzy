# Guia de Localização - Projeto Ticktzy

Este documento serve como um mapa completo para localizar cada parte da aplicação: telas, lógicas de negócio, funções e arquivos relacionados.

---

## 📂 Estrutura de Pastas - Visão Geral

```
app/src/main/java/br/com/sttsoft/ticktzy/
├── core/                    # Componentes base reutilizáveis
├── domain/                  # Lógica de negócio (Use Cases)
├── features/                 # Features com MVVM
├── presentation/             # Interface do usuário
├── repository/               # Camada de dados
└── extensions/               # Extensões Kotlin
```

---

## 🏗️ 1. CORE - Componentes Base

### 📍 `core/device/`

**Função**: Controle de dispositivos físicos (impressoras)

| Arquivo | O que faz |
|---------|-----------|
| `PrinterController.kt` | Interface para controle de impressora |
| `PrinterControllerSunmi.kt` | Implementação específica para impressoras Sunmi |
| `PosmpReceiptController.kt` | Interface para impressão POSMP |
| `PosmpReceiptControllerImpl.kt` | Implementação para impressão POSMP |

**Como usar**: Acesso via `printerController` em Activities que herdam de `BaseActivity`

---

### 📍 `core/prefs/`

**Função**: Gerenciamento de preferências (SharedPreferences)

| Arquivo | O que faz |
|---------|-----------|
| `PrefsGateway.kt` | Interface para acesso a preferências |
| `PrefsGatewayImpl.kt` | Implementação concreta (SharedPreferences) |

**Chaves importantes armazenadas**:
- `CAIXA`: Valor atual do caixa
- `CAIXA_INICIAL`: Valor inicial do caixa
- `CAIXA_ABERTO`: Status do caixa
- `SALES_MADE`: Total de vendas
- `{TYPE}_TYPE`: Contadores por tipo de pagamento
- `{TYPE}_VALUE`: Valores por tipo de pagamento

---

### 📍 `core/result/`

**Função**: Tratamento de resultados de operações

| Arquivo | O que faz |
|---------|-----------|
| `AppResult.kt` | Sealed class para sucesso/erro |
| `ErrorModel.kt` | Modelo de erro estruturado |
| `ErrorMapper.kt` | Mapeamento de erros |

**Exemplo**:
```kotlin
sealed class AppResult<out T> {
    data class Success<T>(val data: T): AppResult<T>()
    data class Failure(val error: ErrorModel): AppResult<Nothing>()
}
```

---

### 📍 `core/ui/`

**Função**: Componentes de UI base (MVVM)

| Arquivo | O que faz |
|---------|-----------|
| `BaseViewModel.kt` | ViewModel base com StateFlow e Channel |
| `UiState.kt` | Interface para estados da UI |
| `UiEvent.kt` | Interface para eventos da UI |
| `UiEffect.kt` | Interface para efeitos colaterais |
| `defaultVmFactory.kt` | Factory para criar ViewModels |
| `viewbinding/collect.kt` | Extensões para coletar flows |

---

## 🧠 2. DOMAIN - Lógica de Negócio

### 📍 `domain/` (Use Cases)

**Função**: Encapsular lógica de negócio isolada

| Arquivo | O que faz | Onde é usado |
|---------|-----------|--------------|
| `GetDadosSubUseCase.kt` | Busca dados de subaquirente | Splash, Config |
| `GetInfosUseCase.kt` | Busca informações do terminal | SplashActivity |
| `GetProductsUseCase.kt` | Busca produtos do servidor | ProductSyncUseCase |
| `ProductCacheUseCase.kt` | Cache local de produtos | Splash, Sale |
| `ProductSyncUseCase.kt` | Sincroniza produtos (servidor ↔ local) | SplashActivity |
| `PrinterUseCase.kt` | Lógica de impressão | Sale, Cashier |
| `SitefUseCase.kt` | Integração com SiTef (pagamentos) | SaleActivity |
| `TerminalnfosUseCase.kt` | Informações do terminal | SplashActivity |
| `VerifyTlsComunicationUseCase.kt` | Verifica TLS | Config |

---

## 🎨 3. FEATURES - Funcionalidades

### 📍 `features/sale/`

**Função**: Feature de vendas com MVVM completo

| Arquivo | O que faz |
|---------|-----------|
| `SaleState.kt` | Define estado da tela de vendas |
| `SaleEvent.kt` | Define eventos (Load, Search, Pay, etc) |
| `SaleEffect.kt` | Define efeitos (Toast, Navegação, Print) |
| `SaleViewModel.kt` | Gerencia lógica de vendas |
| `ui/SalesActivity.kt` | Activity de vendas |

**Eventos principais**:
- `Load`: Carrega produtos
- `Search`: Busca produtos
- `CashPay`: Pagamento em dinheiro
- `PixPay`: Pagamento PIX
- `CardPay`: Pagamento com cartão

---

## 📱 4. PRESENTATION - Interface do Usuário

### 📍 `presentation/base/`

**Função**: Activities base com funcionalidades compartilhadas

| Arquivo | O que faz |
|---------|-----------|
| `BaseActivity.kt` | Activity base com binding de impressoras, diálogos, etc |
| `SplashActivity.kt` | Tela inicial que sincroniza dados |

**Funcionalidades de BaseActivity**:
- Binding de impressoras Sunmi e POSMP
- Dialog de loading
- Toast
- Controle de navegação

---

### 📍 `presentation/home/`

**Função**: Tela principal

| Arquivo | O que faz |
|---------|-----------|
| `HomeActivity.kt` | Menu principal com navegação |
| **Layout**: `activity_home.xml` | Layout com botões de ação |

**Botões**:
- Nova Venda → `SaleActivity`
- Cobrança → `ChargeActivity`
- Relatórios (não implementado)
- Config → `ConfigActivity`
- Funções do Caixa → `ActivityCashierHome`
- Funções Administrativas → `ActivitySitefHome`

**Layout**: `app/src/main/res/layout/activity_home.xml`

---

### 📍 `presentation/sale/`

**Função**: Módulo de vendas

| Arquivo | O que faz |
|---------|-----------|
| `ui/SaleActivity.kt` | Tela principal de vendas |
| `components/PaymentBar.kt` | Barra de pagamentos (PIX, Cartão) |
| `components/ProductAdapter.kt` | Adapter para lista de produtos |
| `components/ProductViewHolder.kt` | ViewHolder de produto |
| `components/SearchBar.kt` | Barra de busca |

**Layouts**:
- `activity_sale.xml`: Layout principal
- `component_payment_bar.xml`: Barra de pagamento
- `component_product_item.xml`: Item de produto
- `component_search_bar.xml`: Barra de busca

**Fluxo de Venda**:
```
SaleActivity → Adicionar Produtos → Escolher Pagamento → Processar → Imprimir → Finalizar
```

---

### 📍 `presentation/cashier/start/`

**Função**: Abertura de caixa

| Arquivo | O que faz |
|---------|-----------|
| `ActivityCashierStart.kt` | Interface para abertura de caixa |
| `ActivityCashierBase.kt` | Lógica compartilhada (base para Start/Finish) |

**Layout**: `app/src/main/res/layout/activity_cashier_start.xml`

**Fluxo**:
```
ActivityCashierStart → Verifica se caixa já está aberto → Pede valor inicial → Imprime comprovante → Navega para Home
```

---

### 📍 `presentation/cashier/home/`

**Função**: Home do caixa (menu de operações)

| Arquivo | O que faz |
|---------|-----------|
| `ActivityCashierHome.kt` | Menu com opções de caixa |

**Opções**:
- Reforço de caixa
- Sangria
- Fechamento

**Layout**: `app/src/main/res/layout/activity_cashier_home.xml`

---

### 📍 `presentation/cashier/finish/`

**Função**: Fechamento de caixa

| Arquivo | O que faz |
|---------|-----------|
| `ActivityCashierFinish.kt` | Mostra relatório e fecha caixa |
| `components/tableInfos.kt` | Data class para itens da tabela |
| `components/TableInfosAdapter.kt` | Adapter para exibir informações |

**Layout**: `app/src/main/res/layout/activity_cashier_finish.xml`

**Componentes**:
- `component_table_info_row.xml`: Linha da tabela
- `component_table_info_section.xml`: Seção da tabela

**Informações exibidas**:
- Total de vendas
- Total de cobranças
- Sangrias e reforços realizados
- Resumo por tipo de pagamento
- Valores finais

---

### 📍 `presentation/cashier/reinforce/`

**Função**: Reforço de caixa

| Arquivo | O que faz |
|---------|-----------|
| `CashierReinforceActivity.kt` | Interface para reforço de caixa |

**Layout**: Em `app/src/main/res/layout/`

---

### 📍 `presentation/cashier/sangria/`

**Função**: Sangria de caixa

| Arquivo | O que faz |
|---------|-----------|
| `CashierSangriaActivity.kt` | Interface para sangria de caixa |

**Layout**: Em `app/src/main/res/layout/`

---

### 📍 `presentation/config/`

**Função**: Configurações do sistema

| Arquivo | O que faz |
|---------|-----------|
| `ConfigActivity.kt` | Configurações gerais |
| `ConfigSitefInfosActivity.kt` | Configuração SiTef |
| `ConfigTicketActivity.kt` | Configuração de impressão |

**Layouts**: `activity_config.xml`, etc.

---

### 📍 `presentation/dialogs/`

**Função**: Diálogos reutilizáveis

| Arquivo | O que faz | Quando usar |
|---------|-----------|-------------|
| `ChangeDialog.kt` | Diálogo de troco | Vendas em dinheiro |
| `ConfirmDialog.kt` | Diálogo de confirmação | Confirmações gerais |
| `InputDialog.kt` | Diálogo de input | Entrada de dados |
| `LoadingDialogFragment.kt` | Loading | Operações assíncronas |
| `PaymentTypeChooseDialog.kt` | Escolha de pagamento | Escolha entre débito/crédito |

**Layouts**: Em `app/src/main/res/layout/dialog_*.xml`

---

### 📍 `presentation/charge/`

**Função**: Cobrança

| Arquivo | O que faz |
|---------|-----------|
| `ChargeActivity.kt` | Interface de cobrança |

**Layout**: `activity_charge.xml`

---

### 📍 `presentation/sitef/`

**Função**: Home administrativo SiTef

| Arquivo | O que faz |
|---------|-----------|
| `ActivitySitefHome.kt` | Menu administrativo SiTef |

**Layout**: `activity_sitef_home.xml`

---

## 💾 5. REPOSITORY - Camada de Dados

### 📍 `repository/local/`

**Função**: Cache e armazenamento local

| Arquivo | O que faz |
|---------|-----------|
| `product.kt` | Modelo de produto para cache |

**Armazenamento**: SharedPreferences

---

### 📍 `repository/remote/`

**Função**: Comunicação com APIs

#### **Interfaces Retrofit** (`repositorys/`)

| Arquivo | O que faz |
|---------|-----------|
| `InfoRepository.kt` | Busca informações do terminal |
| `ProductsRepository.kt` | Busca produtos do servidor |

#### **Clientes HTTP** (`root/`)

| Arquivo | O que faz |
|---------|-----------|
| `MSCall.kt` | Client Retrofit base |
| `MSCallProducts.kt` | Client para produtos (Back4App) |

#### **Modelos de Requisição** (`request/`)

| Arquivo | O que faz |
|---------|-----------|
| `Terminal.kt` | Dados do terminal |
| `TerminalWrapper.kt` | Wrapper de terminal |

#### **Modelos de Resposta** (`response/`)

| Arquivo | O que faz |
|---------|-----------|
| `ProductsResponse.kt` | Resposta de produtos |
| `InfoResponse.kt` | Informações do terminal |
| `Sitef.kt`, `LojaSitef.kt`, `Pagamento.kt` | Dados relacionados a pagamento |
| `App.kt` | Configurações do app |
| `AtualizacaoRemota.kt` | Atualizações remotas |
| `Subadquirencia.kt` | Dados de subaquirente |

---

## 🔧 6. EXTENSIONS - Extensões Kotlin

### 📍 `extensions/`

**Função**: Facilitar operações comuns

| Arquivo | O que faz | Métodos principais |
|---------|-----------|---------------------|
| `DoubleExt.kt` | Extensões para Double | `toReal()`, `toRealFormatado()` |
| `ImageViewExt.kt` | Extensões para ImageView | Carregamento de imagens |
| `LongExt.kt` | Extensões para Long | `toReal()`, conversões |
| `NetworkExt.kt` | Extensões de rede | Verificações de conectividade |
| `SharedPrefsExtensions.kt` | Extensões para Prefs | `savePref()`, `getPref()`, `saveToPrefs()`, `getFromPrefs()` |
| `StringExt.kt` | Extensões para String | Formatações |

---

## 🗂️ Layouts XML

### 📍 `app/src/main/res/layout/`

**Principais layouts**:

| Layout | O que é | Usado por |
|--------|---------|-----------|
| `activity_home.xml` | Tela principal | HomeActivity |
| `activity_sale.xml` | Tela de vendas | SaleActivity |
| `activity_splash.xml` | Splash screen | SplashActivity |
| `activity_cashier_start.xml` | Abertura de caixa | ActivityCashierStart |
| `activity_cashier_finish.xml` | Fechamento de caixa | ActivityCashierFinish |
| `activity_cashier_home.xml` | Home do caixa | ActivityCashierHome |
| `component_payment_bar.xml` | Barra de pagamento | PaymentBar |
| `component_product_item.xml` | Item de produto | ProductAdapter |
| `component_search_bar.xml` | Barra de busca | SearchBar |
| `dialog_*.xml` | Diálogos | Dialogs |

---

## 🎯 Localização por Funcionalidade

### Como encontrar "Tela de Vendas"?
```
📁 presentation/sale/ui/SaleActivity.kt
📁 presentation/sale/components/
    - PaymentBar.kt
    - ProductAdapter.kt
    - SearchBar.kt
📁 features/sale/
    - SaleState.kt
    - SaleEvent.kt
    - SaleViewModel.kt
```

### Como encontrar "Lógica de Impressão"?
```
📁 domain/PrinterUseCase.kt
📁 core/device/
    - PrinterController.kt
    - PrinterControllerSunmi.kt
📁 presentation/base/BaseActivity.kt (bindPrinter)
```

### Como encontrar "Integração SiTef"?
```
📁 domain/SitefUseCase.kt
📁 presentation/sitef/ActivitySitefHome.kt
📁 repository/remote/response/ (modelos SiTef)
```

### Como encontrar "Gerenciamento de Caixa"?
```
📁 presentation/cashier/
    - start/ActivityCashierStart.kt
    - home/ActivityCashierHome.kt
    - finish/ActivityCashierFinish.kt
    - reinforce/CashierReinforceActivity.kt
    - sangria/CashierSangriaActivity.kt
```

### Como encontrar "Cache de Produtos"?
```
📁 domain/ProductCacheUseCase.kt
📁 domain/ProductSyncUseCase.kt
📁 repository/local/product.kt
📁 extensions/SharedPrefsExtensions.kt
```

---

## 🔍 Busca Rápida de Arquivos

### Se você quer...

**Modificar lista de produtos**: `presentation/sale/ui/SaleActivity.kt` + `components/ProductAdapter.kt`

**Adicionar novo tipo de pagamento**: `features/sale/SaleViewModel.kt` + `SaleEvent.kt`

**Implementar impressão**: `domain/PrinterUseCase.kt` + `core/device/`

**Integrar nova API**: `repository/remote/MSCall.kt` + `repositorys/*.kt`

**Adicionar nova tela**: `presentation/[feature]/[Activity].kt` + layout em `res/layout/`

**Extensões de tipos**: `extensions/*.kt`

**Use Cases**: `domain/*UseCase.kt`

---

## 📊 Mapa de Navegação

```
SplashActivity (entry point)
    ↓
ActivityCashierStart (se caixa fechado)
    ↓
HomeActivity
    ├─→ SaleActivity
    │     ├─→ PaymentTypeChooseDialog
    │     ├─→ ChangeDialog (dinheiro)
    │     └─→ Finish (imprime tickets)
    │
    ├─→ ChargeActivity
    │
    ├─→ ConfigActivity
    │     ├─→ ConfigSitefInfosActivity
    │     └─→ ConfigTicketActivity
    │
    └─→ ActivityCashierHome (com senha)
          ├─→ Reforço
          ├─→ Sangria
          └─→ ActivityCashierFinish
```

---

## 🎨 Recursos Visuais

### 📍 `app/src/main/res/`

#### **drawable/**: Ícones e imagens de background
- `ic_*.xml`: Ícones vetoriais
- `bg_*.xml`: Backgrounds de componentes
- `button_*.xml`: Estilos de botões
- `component_*.xml`: Backgrounds de componentes

#### **layout/**: Layouts XML
- `activity_*.xml`: Activities
- `component_*.xml`: Componentes reutilizáveis
- `dialog_*.xml`: Diálogos

#### **values/**: Recursos de valores
- `strings.xml`: Textos
- `colors.xml`: Cores
- `dimens.xml`: Dimensões
- `themes.xml`: Temas

---

## 💡 Dicas de Navegação

1. **Classes base**: Sempre comece por `BaseActivity` e `BaseViewModel`
2. **Use Cases**: Sempre no diretório `domain/`
3. **State management**: Consulte `core/ui/` para entender o padrão
4. **Layouts**: Sempre em `res/layout/` com nome correspondente à Activity
5. **Dialogs**: Em `presentation/dialogs/`
6. **API**: Em `repository/remote/`

---

Este guia deve cobrir todas as suas necessidades de localização no projeto! 🎯

