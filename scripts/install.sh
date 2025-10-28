#!/bin/bash

# Script para instalar o APK no dispositivo Android via ADB
# Uso: ./scripts/install.sh [opções]
# Opções:
#   -a, --apk PATH    Caminho do APK (padrão: app/build/outputs/apk/debug/app-debug.apk)
#   -h, --help        Mostrar ajuda
#   -o, --open        Abrir o app após instalar
#   -r, --reinstall   Reinstalar o app (desinstala primeiro)

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
PACKAGE_NAME="br.com.sttsoft.ticktzy"
SHOULD_REINSTALL=false
SHOULD_OPEN=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -a|--apk)
            APK_PATH="$2"
            shift 2
            ;;
        -h|--help)
            echo "Uso: $0 [opções]"
            echo ""
            echo "Opções:"
            echo "  -a, --apk PATH     Caminho do APK (padrão: app/build/outputs/apk/debug/app-debug.apk)"
            echo "  -h, --help         Mostrar esta ajuda"
            echo "  -o, --open         Abrir o app após instalar"
            echo "  -r, --reinstall    Reinstalar o app (desinstala primeiro)"
            exit 0
            ;;
        -o|--open)
            SHOULD_OPEN=true
            shift
            ;;
        -r|--reinstall)
            SHOULD_REINSTALL=true
            shift
            ;;
        *)
            echo "Opção desconhecida: $1"
            echo "Use -h ou --help para ver a ajuda"
            exit 1
            ;;
    esac
done

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "🔧 Instalador de APK para Android via ADB"
echo "=========================================="
echo ""

# Verificar se o APK existe
if [ ! -f "$APK_PATH" ]; then
    echo -e "${RED}❌ Erro: APK não encontrado em: $APK_PATH${NC}"
    echo ""
    echo "Use './gradlew assembleDebug' para gerar o APK primeiro"
    exit 1
fi

echo -e "${GREEN}✓${NC} APK encontrado: $APK_PATH"
echo ""

# Verificar se adb está disponível
if ! command -v adb &> /dev/null; then
    echo -e "${RED}❌ Erro: ADB não encontrado${NC}"
    echo ""
    echo "Instale o Android SDK Platform Tools:"
    echo "  brew install android-platform-tools"
    echo "  ou baixe de: https://developer.android.com/studio/releases/platform-tools"
    exit 1
fi

echo -e "${GREEN}✓${NC} ADB disponível"
echo ""

# Verificar se há dispositivos conectados
DEVICES=$(adb devices | grep -v "List" | grep "device$")
if [ -z "$DEVICES" ]; then
    echo -e "${YELLOW}⚠ Nenhum dispositivo conectado${NC}"
    echo ""
    echo "Conecte um dispositivo via USB e:"
    echo "  1. Ative 'Depuração USB' nas Opções do Desenvolvedor"
    echo "  2. Autorize o computador quando solicitado"
    echo ""
    echo "Conectado via WiFi? Use 'adb connect IP:PORT'"
    exit 1
fi

echo -e "${GREEN}✓${NC} Dispositivo conectado"
echo ""
echo "Dispositivo(s):"
adb devices | grep "device$"
echo ""

# Reinstalar se solicitado
if [ "$SHOULD_REINSTALL" = true ]; then
    echo -e "${YELLOW}♻ Reinstalando...${NC}"
    echo "Desinstalando versão anterior..."
    adb uninstall "$PACKAGE_NAME" 2>/dev/null || true
    echo -e "${GREEN}✓${NC} Versão anterior desinstalada"
    echo ""
fi

# Instalar o APK
echo "📱 Instalando APK..."
if adb install "$APK_PATH"; then
    echo ""
    echo -e "${GREEN}✅ APK instalado com sucesso!${NC}"
    echo ""
    
    # Abrir o app se solicitado
    if [ "$SHOULD_OPEN" = true ]; then
        echo "🚀 Abrindo o app..."
        adb shell monkey -p "$PACKAGE_NAME" -c android.intent.category.LAUNCHER 1
        echo -e "${GREEN}✓${NC} App aberto"
    fi
else
    echo ""
    echo -e "${RED}❌ Falha ao instalar o APK${NC}"
    echo ""
    echo "Possíveis causas:"
    echo "  • Dispositivo não autorizado (verifique no dispositivo)"
    echo "  • APK com assinatura diferente (use -r para reinstalar)"
    echo "  • Espaço insuficiente no dispositivo"
    exit 1
fi

