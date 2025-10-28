# Guia de ADB - Android Debug Bridge

Guia completo de comandos ADB (Android Debug Bridge) para desenvolvimento e debug do projeto Ticktzy.

---

## 📋 Índice

1. [O que é ADB?](#o-que-é-adb)
2. [Instalação e Configuração](#instalação-e-configuração)
3. [Comandos Básicos](#comandos-básicos)
4. [Comandos de Dispositivo](#comandos-de-dispositivo)
5. [Comandos de Instalação](#comandos-de-instalação)
6. [Comandos de Debug](#comandos-de-debug)
7. [Comandos de Log](#comandos-de-log)
8. [Comandos de Arquivos](#comandos-de-arquivos)
9. [Comandos Específicos do Projeto](#comandos-específicos-do-projeto)
10. [Troubleshooting ADB](#troubleshooting-adb)

---

## 🔧 O que é ADB?

**Android Debug Bridge (ADB)** é uma ferramenta de linha de comando que permite comunicação entre um computador e dispositivos Android. Ele permite:

- Instalar e desinstalar aplicativos
- Enviar e receber arquivos
- Executar comandos shell no dispositivo
- Depurar aplicativos
- Capturar logs
- Acessar banco de dados SQLite
- Monitorar CPU e memória

---

## 💻 Instalação e Configuração

### macOS

```bash
# Já incluído no Android SDK
# Adicionar ao PATH (se não estiver):
export PATH=$PATH:~/Library/Android/sdk/platform-tools

# Verificar instalação
adb version
```

### Linux

```bash
# Instalar via apt
sudo apt install android-tools-adb

# OU adicionar SDK tools ao PATH
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

### Windows

O ADB já vem com o Android Studio. Adicionar ao PATH:
```
C:\Users\SEU_USUARIO\AppData\Local\Android\Sdk\platform-tools
```

---

## 🎯 Comandos Básicos

### Verificar Versão

```bash
adb version
# Output esperado:
# Android Debug Bridge version 1.0.41
```

### Iniciar/Parar Servidor ADB

```bash
# Iniciar servidor
adb start-server

# Parar servidor (útil para resolver problemas de conexão)
adb kill-server
adb start-server

# Ver status
adb status
```

---

## 📱 Comandos de Dispositivo

### Listar Dispositivos Conectados

```bash
# Listar dispositivos
adb devices

# Listar dispositivos com detalhes
adb devices -l

# Output esperado:
# List of devices attached
# ABC123DEF456    device
# emulator-5554   device
```

**Estados possíveis**:
- `device` - Conectado e autorizado
- `offline` - Conectado mas não respondendo
- `unauthorized` - Dispositivo não autorizou este computador

### Informações do Dispositivo

```bash
# Obter serial number
adb get-serialno

# Obter estado do dispositivo
adb get-state
# Output: device, offline, unknown

# Informações detalhadas
adb shell getprop ro.build.version.release  # Versão Android
adb shell getprop ro.product.model          # Modelo do dispositivo
adb shell getprop ro.product.manufacturer  # Fabricante

# Todos os produtos do dispositivo
adb shell getprop | grep ro.product
```

### Reiniciar Dispositivo

```bash
# Reiniciar em modo normal
adb reboot

# Reiniciar em bootloader
adb reboot bootloader

# Reiniciar em recovery
adb reboot recovery

# Aguardar dispositivo voltar
adb wait-for-device
```

---

## 📲 Comandos de Instalação

### Instalar APK

```bash
# Instalar APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Forçar reinstalação (replace)
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Permitir downgrade de versão
adb install -d app/build/outputs/apk/debug/app-debug.apk

# Instalar em múltiplos APKs (split APKs)
adb install-multiple base.apk split.apk

# Ver progresso detalhado
adb install -r -t app-debug.apk
```

### Desinstalar Aplicativo

```bash
# Desinstalar usando package name
adb uninstall br.com.sttsoft.ticktzy

# Desinstalar mantendo dados e cache
adb uninstall -k br.com.sttsoft.ticktzy
```

### Informações do Aplicativo

```bash
# Listar todos os pacotes instalados
adb shell pm list packages

# Filtrar por prefixo
adb shell pm list packages | grep ticktzy

# Informações detalhadas do app
adb shell dumpsys package br.com.sttsoft.ticktzy

# Caminho da APK instalada
adb shell pm path br.com.sttsoft.ticktzy
```

---

## 🐛 Comandos de Debug

### Acessar Shell do Android

```bash
# Entrar no shell
adb shell

# Comandos úteis dentro do shell:
ls -la                              # Listar arquivos
cd /data/data/br.com.sttsoft.ticktzy # Acessar dados do app
ps aux                              # Ver processos
top                                 # Monitor de recursos
```

### Executar Comando no Shell

```bash
# Sem entrar no shell
adb shell pm list packages
adb shell ls -la /data/data/
adb shell dumpsys meminfo

# Com permissões de root
adb shell su -c "comando"
```

### Limpar Dados do Aplicativo

```bash
# Limpar todos os dados (cache + dados)
adb shell pm clear br.com.sttsoft.ticktzy

# Limpar apenas cache
adb shell pm clear-cache br.com.sttsoft.ticktzy
```

### Abrir Activity/Fragment

```bash
# Abrir SplashActivity
adb shell am start -n br.com.sttsoft.ticktzy/.presentation.base.SplashActivity

# Abrir HomeActivity
adb shell am start -n br.com.sttsoft.ticktzy/.presentation.home.HomeActivity

# Abrir SaleActivity
adb shell am start -n br.com.sttsoft.ticktzy/.presentation.sale.ui.SaleActivity

# Com intent extras
adb shell am start -n br.com.sttsoft.ticktzy/.presentation.sale.ui.SaleActivity \
    --es "extra_key" "extra_value"
```

### Fechar Aplicativo

```bash
# Forçar stop do app
adb shell am force-stop br.com.sttsoft.ticktzy

# Matar processo
adb shell kill $(adb shell pidof br.com.sttsoft.ticktzy)
```

---

## 📊 Comandos de Log

### Capturar Logs

```bash
# Capturar todos os logs
adb logcat

# Capturar logs do Ticktzy apenas
adb logcat | grep ticktzy

# Capturar logs de erro
adb logcat *:E

# Capturar logs do Android (sistema)
adb logcat AndroidRuntime:E

# Salvar logs em arquivo
adb logcat > logcat.txt

# Limpar logs e capturar novos
adb logcat -c && adb logcat
```

### Filtrar Logs por Tag

```bash
# Ver apenas logs com tag específica
adb logcat -s ticktzy

# Múltiplas tags
adb logcat -s ticktzy:D SiTef:D Posmp:D

# Níveis de log:
# V - Verbose
# D - Debug
# I - Info
# W - Warning
# E - Error
# F - Fatal
```

### Exemplos Práticos

```bash
# Logs de erro do sistema + ticktzy
adb logcat *:E ticktzy:D

# Monitorar em tempo real apenas erros
adb logcat *:E

# Logs com data/hora
adb logcat -v time

# Logs com data/hora e PID
adb logcat -v long

# Capturar e salvar logs de crash
adb logcat -c
# Reproduzir crash
adb logcat -d > crash.txt
```

---

## 📁 Comandos de Arquivos

### Transferir Arquivos

```bash
# Copiar arquivo do computador para dispositivo
adb push arquivo.txt /sdcard/

# Copiar arquivo do dispositivo para computador
adb pull /sdcard/arquivo.txt ./

# Com permissões de root
adb push arquivo.txt /data/data/br.com.sttsoft.ticktzy/

# Forçar SID (para grandes arquivos)
adb push -z gzip arquivo.apk /sdcard/
```

### Acessar Arquivos do Aplicativo

```bash
# Listar arquivos do app
adb shell run-as br.com.sttsoft.ticktzy ls -la

# Copiar SharedPreferences
adb exec-out run-as br.com.sttsoft.ticktzy \
    cat shared_prefs/prefs.xml > prefs.xml

# Copiar banco de dados
adb exec-out run-as br.com.sttsoft.ticktzy \
    cat databases/app.db > app.db

# Acessar cache
adb shell run-as br.com.sttsoft.ticktzy ls -la cache/
```

### Backup e Restore

```bash
# Backup completo do app
adb backup -apk br.com.sttsoft.ticktzy -f backup.ab

# Restaurar backup
adb restore backup.ab

# Backup com dados específicos
adb backup -all -f backup_completo.ab
```

---

## 🎮 Comandos Específicos do Projeto

### Instalação e Teste Rápido

```bash
# Build e install em um comando
./gradlew installDebug

# Build, install e capturar logs
./gradlew installDebug && adb logcat -c && adb logcat ticktzy:D
```

### Limpar e Reinstalar

```bash
# Desinstalar completamente
adb uninstall br.com.sttsoft.ticktzy

# Limpar build
./gradlew clean

# Build e install
./gradlew assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Debug de Impressora

```bash
# Verificar se impressora está conectada
adb shell dumpsys | grep printer

# Logs específicos de impressão
adb logcat | grep -i "printer\|sunmi\|posmp"
```

### Debug de Pagamento (SiTef)

```bash
# Logs do SiTef
adb logcat | grep -i "sitef\|pagamento"

# Forçar fechamento do fluxo de pagamento
adb shell am force-stop br.com.softwareexpress.sitef.msitef

# Dumpsys do sistema de pagamento
adb shell dumpsys | grep -A 10 "SiTef"
```

### Gerenciar Permissões

```bash
# Listar permissões do app
adb shell dumpsys package br.com.sttsoft.ticktzy | grep permission

# Conceder permissão manualmente
adb shell pm grant br.com.sttsoft.ticktzy android.permission.CAMERA

# Revogar permissão
adb shell pm revoke br.com.sttsoft.ticktzy android.permission.CAMERA
```

### Monitorar Memória e CPU

```bash
# Memória do app
adb shell dumpsys meminfo br.com.sttsoft.ticktzy

# CPU do app
adb shell top -p $(adb shell pidof br.com.sttsoft.ticktzy)

# Monitor contínuo
watch -n 1 adb shell dumpsys meminfo br.com.sttsoft.ticktzy
```

### Acessar SharedPreferences

```bash
# Ver preferências
adb shell run-as br.com.sttsoft.ticktzy cat shared_prefs/prefs.xml

# Listar todas as preferências
adb shell run-as br.com.sttsoft.ticktzy ls -la shared_prefs/
```

---

## 🛠️ Comandos Avançados

### Screenshot

```bash
# Capturar screenshot
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png

# OU direto
adb exec-out screencap -p > screenshot.png
```

### Screen Recording

```bash
# Gravar tela (10 segundos)
adb shell screenrecord /sdcard/demo.mp4
# Pressione Ctrl+C para parar

# Baixar vídeo
adb pull /sdcard/demo.mp4
```

### Simular Input

```bash
# Tocar na tela (x, y)
adb shell input tap 500 1000

# Swipe
adb shell input swipe 300 500 300 1000

# Texto
adb shell input text "teste123"

# Key event
adb shell input keyevent KEYCODE_BACK
adb shell input keyevent KEYCODE_HOME
```

### Acessar SQLite Database

```bash
# Entrar no shell do app
adb shell run-as br.com.sttsoft.ticktzy

# Executar SQLite
sqlite3 databases/app.db

# Queries úteis:
SELECT * FROM table;
.tables          # Listar tabelas
.schema          # Ver schema
.quit            # Sair
```

### Monkey Testing

```bash
# Teste com monkey (1000 eventos aleatórios)
adb shell monkey -p br.com.sttsoft.ticktzy -v 1000

# Apenas toques
adb shell monkey -p br.com.sttsoft.ticktzy \
    -v --throttle 100 --pct-touch 100 500

# Com seed para reproduzir
adb shell monkey -p br.com.sttsoft.ticktzy \
    -v -s 12345 500
```

---

## 🚨 Troubleshooting ADB

### Dispositivo não aparece em "adb devices"

```bash
# 1. Verificar USB conectado
lsusb  # Linux
system_profiler SPUSBDataType  # macOS

# 2. Reiniciar ADB
adb kill-server
adb start-server
adb devices

# 3. Verificar drivers (Windows)
# Instalar drivers USB do fabricante

# 4. Verificar autorização
# Verificar popup no dispositivo: "Autorizar debugging USB?"
```

### Dispositivo aparece como "unauthorized"

**Solução**:
1. Confirme popup no dispositivo
2. Marque "Sempre autorizar este computador"
3. Reinicie ADB:
```bash
adb kill-server
adb start-server
```

### "Device offline"

```bash
# Reconectar dispositivo
adb kill-server
adb disconnect
adb connect <ip>:5555  # Se usando rede
adb devices
```

### Reset completo de conexão

```bash
# Parar todos os processos ADB
adb kill-server
pkill adb

# Remover dispositivos conhecidos (macOS/Linux)
rm -rf ~/.android/adbkey*
rm -rf ~/.android/adb_usb.ini

# Reiniciar
adb start-server
adb devices
```

### Não consegue acessar dados do app

```bash
# Verificar se app está instalado
adb shell pm path br.com.sttsoft.ticktzy

# Tentar com run-as (se app foi instalado normalmente)
adb shell run-as br.com.sttsoft.ticktzy ls

# Se falhar, pode ser que app foi instalado como sistema
# Tentar com root:
adb root
adb shell ls /data/data/br.com.sttsoft.ticktzy/
```

---

## 📋 Scripts Úteis

### Script de Clean Install

```bash
#!/bin/bash
# clean_install.sh

echo "🧹 Limpando projeto..."
./gradlew clean

echo "🗑️  Desinstalando app..."
adb uninstall br.com.sttsoft.ticktzy

echo "🔨 Build..."
./gradlew assembleDebug

echo "📲 Instalando..."
adb install -r app/build/outputs/apk/debug/app-debug.apk

echo "✅ Concluído!"
adb shell am start -n br.com.sttsoft.ticktzy/.presentation.base.SplashActivity
```

### Script de Logs em Tempo Real

```bash
#!/bin/bash
# logs.sh

# Limpar logs
adb logcat -c

echo "📱 Capturando logs de ticktzy..."
adb logcat | grep --line-buffered -E "(ticktzy|SiTef|Posmp|Sunmi)"
```

### Script de Monitoramento

```bash
#!/bin/bash
# monitor.sh

while true; do
  clear
  echo "=== Memória do Ticktzy ==="
  adb shell dumpsys meminfo br.com.sttsoft.ticktzy | head -20
  echo ""
  echo "=== CPU ==="
  adb shell top -n 1 | grep ticktzy
  sleep 2
done
```

---

## 🎯 Resumo dos Comandos Mais Usados

```bash
# Setup básico
adb kill-server
adb start-server
adb devices

# Build e install
./gradlew installDebug

# Logs
adb logcat | grep ticktzy

# Limpar app
adb shell pm clear br.com.sttsoft.ticktzy

# Abrir app
adb shell am start -n br.com.sttsoft.ticktzy/.presentation.home.HomeActivity

# Screenshot
adb exec-out screencap -p > screenshot.png

# Info do device
adb shell getprop | grep ro.product.model
```

---

## 📚 Referências

- **Android Developers**: https://developer.android.com/studio/command-line/adb
- **ADB Command Reference**: https://developer.android.com/studio/command-line/adb#commandsummary
- **Android Tools**: https://developer.android.com/studio/releases/platform-tools

---

**Última atualização**: Janeiro 2025  
**Versão do ADB**: 34.0.0+

