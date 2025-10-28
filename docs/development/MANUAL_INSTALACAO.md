# Manual de Instalação e Configuração - Projeto Ticktzy

Este manual contém todas as instruções necessárias para configurar o ambiente de desenvolvimento do projeto Ticktzy.

---

## 📋 Índice

1. [Requisitos do Sistema](#requisitos-do-sistema)
2. [Instalação das Ferramentas](#instalação-das-ferramentas)
3. [Configuração do Ambiente](#configuração-do-ambiente)
4. [Configuração do Projeto](#configuração-do-projeto)
5. [Build e Execução](#build-e-execução)
6. [Configuração de APIs](#configuração-de-apis)
7. [Troubleshooting](#troubleshooting)

---

## 🖥️ Requisitos do Sistema

### Hardware Mínimo
- **CPU**: Dual-core 2.0 GHz ou superior
- **RAM**: 8 GB (recomendado 16 GB)
- **Disco**: 40 GB livres
- **GPU**: Aceleração de hardware (opcional, recomendado para emulador)

### Sistema Operacional
- ✅ **macOS**: 10.15 (Catalina) ou superior
- ✅ **Linux**: Ubuntu 18.04+ ou distribuições compatíveis
- ✅ **Windows**: Windows 10 ou superior

---

## 🛠️ Instalação das Ferramentas

### 1. Java Development Kit (JDK)

O projeto requer **Java 21** (Java 25 causa erros com Kotlin 2.1.21).

#### macOS
```bash
# Usando Homebrew
brew install temurin@21

# Ou baixar direto da Microsoft
# https://learn.microsoft.com/en-us/java/openjdk/download
```

#### Linux
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-21-jdk

# Verificar instalação
java -version
```

#### Windows
1. Baixe o JDK 21 da [Microsoft OpenJDK](https://learn.microsoft.com/en-us/java/openjdk/download) ou [Eclipse Temurin](https://adoptium.net/)
2. Execute o instalador
3. Configure a variável de ambiente `JAVA_HOME`

**Importante**: Java 25 NÃO é suportado! Use Java 21.

---

### 2. Android Studio

#### Download
- **Link**: [https://developer.android.com/studio](https://developer.android.com/studio)
- **Versão mínima**: Android Studio Hedgehog | 2023.1.1+

#### Instalação (macOS)
```bash
# Usando Homebrew
brew install --cask android-studio

# Ou download direto do site
# Extrair e mover para Applications/
```

#### Instalação (Linux)
```bash
# Download
wget https://redirector.gvt1.com/edgedl/android/studio/ide-zips/2023.3.1.21/android-studio-2023.3.1.21-linux.tar.gz

# Extrair
tar -xzf android-studio-*.tar.gz
sudo mv android-studio /opt/

# Executar
cd /opt/android-studio/bin
./studio.sh
```

#### Instalação (Windows)
1. Baixe o instalador `.exe`
2. Execute como administrador
3. Siga o wizard de instalação
4. Marque as opções:
   - ✅ Android SDK
   - ✅ Android SDK Platform
   - ✅ Android Virtual Device (AVD)

#### Configuração Inicial
1. Abra Android Studio
2. **Welcome Screen** → Configure → Settings
3. **SDK Manager** → Verifique:
   - ✅ Android SDK Platform 35
   - ✅ Android SDK Build-Tools
   - ✅ Android SDK Command-line Tools
   - ✅ Google Play Services
   - ✅ Google Repository
   - ✅ Google USB Driver (Windows)

---

### 3. Git (Controle de Versão)

#### macOS
```bash
# Já vem instalado, atualizar:
brew install git
```

#### Linux
```bash
sudo apt install git
```

#### Windows
- Download: [https://git-scm.com/download/win](https://git-scm.com/download/win)

---

## ⚙️ Configuração do Ambiente

### 1. Variáveis de Ambiente

#### macOS/Linux
Edite o arquivo `~/.zshrc` ou `~/.bashrc`:

```bash
# Adicionar ao final do arquivo
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
# ou para OpenJDK:
# export JAVA_HOME=/Library/Java/JavaVirtualMachines/ms-21.0.8/Contents/Home

export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/tools
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/emulator
```

Aplicar as mudanças:
```bash
source ~/.zshrc
# ou
source ~/.bashrc
```

#### Windows
1. Painel de Controle → Sistema → Configurações Avançadas
2. Variáveis de Ambiente
3. Adicionar/Editar:

| Variável | Valor |
|----------|-------|
| `JAVA_HOME` | `C:\Program Files\Java\jdk-21` |
| `ANDROID_HOME` | `C:\Users\SEU_USUARIO\AppData\Local\Android\Sdk` |
| `PATH` | Adicionar: `%ANDROID_HOME%\platform-tools` |

---

### 2. Verificar Instalação

Execute os seguintes comandos:

```bash
# Verificar Java
java -version
# Output esperado: openjdk version "21"

# Verificar JDK Home
echo $JAVA_HOME
# Output esperado: caminho para Java 21

# Verificar Android SDK
adb version
# Output esperado: Android Debug Bridge version

# Verificar Gradle
./gradlew --version
```

---

## 📦 Configuração do Projeto

### 1. Clonar o Repositório

```bash
# Via HTTPS
git clone https://github.com/seu-repositorio/ticktzy.git
# ou via SSH
git clone git@github.com:seu-repositorio/ticktzy.git

# Entrar no diretório
cd ticktzy
```

### 2. Configurar Java Home (Importante!)

O projeto está configurado para usar Java 21. Verifique/atualize o arquivo `gradle.properties`:

```properties
# Editar: gradle.properties
org.gradle.java.home=/caminho/para/java-21
```

**Exemplos**:

#### macOS
```properties
# Para Temurin
org.gradle.java.home=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home

# Para Microsoft OpenJDK
org.gradle.java.home=/Users/seu-usuario/Library/Java/JavaVirtualMachines/ms-21.0.8/Contents/Home
```

#### Linux
```properties
org.gradle.java.home=/usr/lib/jvm/java-21-openjdk-amd64
```

#### Windows
```properties
org.gradle.java.home=C\:\\Program Files\\Java\\jdk-21
```

**Encontrar o caminho**:
```bash
# macOS/Linux
/usr/libexec/java_home -V
# ou
ls -la /Library/Java/JavaVirtualMachines/

# Definir automaticamente
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

---

### 3. Sincronizar Dependências

```bash
# Parar daemons do Gradle (se necessário)
./gradlew --stop

# Fazer build pela primeira vez
./gradlew build --refresh-dependencies

# OU simplesmente sincronizar no Android Studio
# File → Sync Project with Gradle Files
```

**Primeira sync pode levar 5-15 minutos** enquanto baixa as dependências.

---

## 🔨 Build e Execução

### 1. Abrir no Android Studio

```bash
# No terminal
open android-studio
# ou
android-studio .

# OU abrir Android Studio e File → Open → selecionar pasta ticktzy
```

### 2. Configurar Emulador ou Dispositivo

#### Emulador Android
1. **AVD Manager**: Tools → Device Manager
2. **Create Device**:
   - Device: Pixel 5 (recomendado)
   - System Image: Android 14 (API 34)
3. **Start**

#### Dispositivo Físico
1. Habilitar **Modo Desenvolvedor**:
   - Configurações → Sobre o Telefone
   - Toque 7x em "Número de Versão"
2. Habilitar **Depuração USB**:
   - Configurações → Opções do Desenvolvedor
   - Marcar "Depuração USB"
3. Conectar via USB
4. Autorizar computador no dispositivo

**Verificar dispositivo conectado**:
```bash
adb devices
# Output esperado:
# List of devices attached
# ABC123    device
```

### 3. Executar Aplicação

#### Via Android Studio
1. Selecionar configuração: **app** (no seletor de run)
2. Selecionar device: Emulador ou dispositivo conectado
3. Clique no botão **Run** (▶️) ou pressione `Shift + F10`

#### Via Terminal
```bash
# Build e install no dispositivo
./gradlew installDebug

# OU diretamente via adb
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk

# Executar testes
./gradlew test

# Build release
./gradlew assembleRelease
```

---

## 🔧 Configuração de APIs

### 1. APIs Back4App

O projeto já contém as chaves de API no código. Se precisar alterar:

**Arquivo**: `app/src/main/java/br/com/sttsoft/ticktzy/repository/remote/repositorys/ProductsRepository.kt`

```kotlin
@Headers(
    "Content-Type: application/json",
    "X-Parse-Application-Id: SUA_APP_ID",
    "X-Parse-REST-API-Key: SUA_API_KEY"
)
```

### 2. API Delta Pag

**Arquivo**: `app/src/main/java/br/com/sttsoft/ticktzy/repository/remote/repositorys/InfoRepository.kt`

```kotlin
@Headers(
    "Content-Type: application/json",
    "Authorization: Bearer SEU_TOKEN_AQUI"
)
```

### 3. URLs de API

**Arquivo**: `app/build.gradle.kts`

```kotlin
buildConfigField("String", "urlDelta", "\"https://pos.deltapag.com.br/\"")
buildConfigField("String", "urlAPI", "\"https://parseapi.back4app.com/\"")
```

---

## 🐛 Troubleshooting

### Erro: "Java version 25 is not supported"

**Causa**: Java 25 instalado não é compatível com Kotlin 2.1.21

**Solução**:
```bash
# Verificar versão Java
java -version

# Instalar Java 21
brew install temurin@21  # macOS
# ou baixar de: https://learn.microsoft.com/en-us/java/openjdk/download

# Configurar JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Atualizar gradle.properties
# Adicionar: org.gradle.java.home=/caminho/para/java-21
```

---

### Erro: "Gradle sync failed"

**Soluções**:
```bash
# 1. Limpar cache
./gradlew clean

# 2. Invalidar caches
# Android Studio: File → Invalidate Caches / Restart

# 3. Deletar .gradle
rm -rf .gradle
./gradlew build

# 4. Atualizar dependências
./gradlew --refresh-dependencies
```

---

### Erro: "SDK location not found"

**Solução**:
1. Verificar `ANDROID_HOME`:
```bash
echo $ANDROID_HOME
# Output esperado: /Users/SEU_USUARIO/Library/Android/sdk
```

2. Se não existir, instalar Android SDK:
   - Android Studio → SDK Manager
   - Instalar Android SDK
   - Configurar `ANDROID_HOME`

3. Editar `local.properties` (gerado automaticamente):
```properties
# macOS
sdk.dir=/Users/SEU_USUARIO/Library/Android/sdk

# Linux
sdk.dir=/home/SEU_USUARIO/Android/Sdk

# Windows
sdk.dir=C\:\\Users\\SEU_USUARIO\\AppData\\Local\\Android\\Sdk
```

---

### Erro: "Unable to resolve host"

**Causa**: Problema de rede/firewall

**Soluções**:
```bash
# Verificar conectividade
ping services.gradle.org

# Configurar proxy (se necessário)
# gradle.properties
systemProp.http.proxyHost=proxy.example.com
systemProp.http.proxyPort=8080
```

---

### Erro: "Printer binding failed"

**Causa**: Impressora Sunmi não disponível no emulador

**Solução**:
- Teste em dispositivo físico Sunmi
- OU desabilite binding de impressora:
```kotlin
override val enablePrinterBinding = false
```

---

### Build Slow

**Otimizações**:

1. **Aumentar memória do Gradle**:
```properties
# gradle.properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m
```

2. **Habilitar caching**:
```properties
# gradle.properties
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.configureondemand=true
```

3. **Desabilitar verificação de assinatura**:
```properties
# gradle.properties
android.enableR8.fullMode=false
```

---

### Dispositivo não reconhecido (adb devices vazio)

**Soluções**:

#### macOS/Linux
```bash
# Adicionar regras USB
sudo nano /etc/udev/rules.d/51-android.rules

# Adicionar:
SUBSYSTEM=="usb", ATTR{idVendor}=="VID", MODE="0664", GROUP="plugdev"

# Reiniciar adb
sudo adb kill-server
adb start-server
```

#### Windows
1. Instalar drivers USB do fabricante do dispositivo
2. Habilitar modo desenvolvedor no dispositivo
3. Autorizar computador no popup do dispositivo

---

### Kotlin Version Conflict

**Erro**: "The binary version of its metadata is XXX"

**Solução**:
```bash
# Limpar build
./gradlew clean

# Rebuild
./gradlew build

# Se persistir, invalidar caches no Android Studio
```

---

## 📱 Configuração Adicional

### 1. Emulador SUNMI (Para testes de impressão)

SUNMI fornece emulador específico para testes de hardware:
- Download: Portal de Desenvolvedor SUNMI
- Configurar AVD com especificações SUNMI

### 2. Teste de Pagamento (SiTef)

Para testar integração SiTef:
- Dispositivo físico necessário
- Instalar SDK SiTef
- Configurar credenciais de teste

### 3. Keystore de Assinatura

Para builds de release:
```bash
# Gerar keystore
keytool -genkey -v -keystore ticktzy.keystore -alias ticktzy -keyalg RSA -keysize 2048 -validity 10000

# Configurar em app/build.gradle.kts
signingConfigs {
    create("release") {
        storeFile = file("caminho/para/ticktzy.keystore")
        storePassword = "senha"
        keyAlias = "ticktzy"
        keyPassword = "senha"
    }
}
```

---

## 🎯 Checklist de Setup

- [ ] Java 21 instalado e configurado
- [ ] Android Studio instalado
- [ ] Android SDK (API 34, 35) instalado
- [ ] Variáveis de ambiente configuradas
- [ ] Repositório clonado
- [ ] Gradle sync completo sem erros
- [ ] Dispositivo/Emulador configurado
- [ ] Build do projeto bem-sucedido
- [ ] App rodando no dispositivo
- [ ] APIs configuradas (se necessário)

---

## 📚 Comandos Úteis

```bash
# Build
./gradlew build

# Clean
./gradlew clean

# Testes
./gradlew test
./gradlew connectedAndroidTest

# Analisar dependências
./gradlew dependencies

# Ver tasks disponíveis
./gradlew tasks

# Build release
./gradlew assembleRelease

# Bundle (para Play Store)
./gradlew bundleRelease

# Instalar no dispositivo
./gradlew installDebug

# Desinstalar
adb uninstall br.com.sttsoft.ticktzy

# Logs em tempo real
adb logcat | grep ticktzy
```

---

## 📖 Recursos Adicionais

- **Android Developers**: https://developer.android.com/
- **Kotlin Docs**: https://kotlinlang.org/docs/
- **Gradle Docs**: https://docs.gradle.org/
- **Android Studio Guide**: https://developer.android.com/studio/intro

---

## 🆘 Suporte

Para problemas específicos:
1. Verificar logs do Android Studio
2. Verificar `~/Library/Logs/CustomLogs/` (macOS)
3. Consultar documentação em `Docs/`
4. Abrir issue no repositório

---

**Última atualização**: Janeiro 2025  
**Versão do manual**: 1.0

