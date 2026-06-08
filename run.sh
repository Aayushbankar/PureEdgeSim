#!/usr/bin/env bash
# =============================================================================
#  PureEdgeSim Runner  —  Linux / macOS
#  Usage:  ./run.sh [--no-compile] [extra mvn exec:java args...]
#
#  Behaviour:
#    1. Detects a local JDK/Maven under ~/tools (installed by this project).
#       Falls back to the system java/mvn if neither is found.
#    2. Recompiles with  mvn clean compile  (skip with --no-compile).
#    3. Launches the interactive CLI menu:
#         mvn exec:java -Dexec.mainClass=...MainApplication
# =============================================================================

set -e

# ── colour helpers ────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; CYAN='\033[0;36m'
YELLOW='\033[1;33m'; BOLD='\033[1m'; RESET='\033[0m'

banner() {
  echo ""
  echo -e "${CYAN}${BOLD}╔══════════════════════════════════════════════════════╗${RESET}"
  echo -e "${CYAN}${BOLD}║          PureEdgeSim  —  Runner Script               ║${RESET}"
  echo -e "${CYAN}${BOLD}╚══════════════════════════════════════════════════════╝${RESET}"
  echo ""
}

info()    { echo -e "  ${GREEN}[✔]${RESET} $*"; }
warn()    { echo -e "  ${YELLOW}[!]${RESET} $*"; }
err()     { echo -e "  ${RED}[✘]${RESET} $*" >&2; exit 1; }
step()    { echo -e "\n${BOLD}▶  $*${RESET}"; }

# ── locate project root (where pom.xml lives) ─────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"

if [ ! -f "$PROJECT_ROOT/pom.xml" ]; then
  err "pom.xml not found in $PROJECT_ROOT — run this script from the project root."
fi

banner

# ── parse flags ───────────────────────────────────────────────────────────────
SKIP_COMPILE=false
EXTRA_ARGS=()
for arg in "$@"; do
  case "$arg" in
    --no-compile) SKIP_COMPILE=true ;;
    *)            EXTRA_ARGS+=("$arg") ;;
  esac
done

# ── discover JAVA_HOME ────────────────────────────────────────────────────────
step "Locating Java..."

LOCAL_JDK=$(find "$HOME/tools" -maxdepth 1 -type d -name 'jdk*' 2>/dev/null | sort -r | head -1)

if [ -n "$LOCAL_JDK" ] && [ -x "$LOCAL_JDK/bin/java" ]; then
  export JAVA_HOME="$LOCAL_JDK"
  info "Using bundled JDK  → $JAVA_HOME"
elif command -v java &>/dev/null; then
  JAVA_HOME_GUESS=$(java -XshowSettings:property -version 2>&1 | grep 'java.home' | awk '{print $NF}')
  # Strip /jre suffix if present
  JAVA_HOME_GUESS="${JAVA_HOME_GUESS%/jre}"
  export JAVA_HOME="$JAVA_HOME_GUESS"
  info "Using system Java  → $(java -version 2>&1 | head -1)"
else
  err "No JDK found. Install JDK 17+ or place a jdk-* folder in ~/tools/."
fi

export PATH="$JAVA_HOME/bin:$PATH"
JAVA_VER=$(java -version 2>&1 | head -1)
info "Java version: $JAVA_VER"

# ── discover Maven ─────────────────────────────────────────────────────────────
step "Locating Maven..."

LOCAL_MVN=$(find "$HOME/tools" -maxdepth 1 -type d -name 'apache-maven*' 2>/dev/null | sort -r | head -1)

if [ -n "$LOCAL_MVN" ] && [ -x "$LOCAL_MVN/bin/mvn" ]; then
  MVN="$LOCAL_MVN/bin/mvn"
  info "Using bundled Maven → $MVN"
elif command -v mvn &>/dev/null; then
  MVN="mvn"
  info "Using system Maven  → $(mvn -version 2>&1 | head -1)"
else
  err "Maven not found. Install Maven or place apache-maven-* in ~/tools/."
fi

export PATH="$(dirname "$MVN"):$PATH"
MVN_VER=$("$MVN" -version 2>&1 | head -1)
info "Maven version: $MVN_VER"

# ── compile ───────────────────────────────────────────────────────────────────
if [ "$SKIP_COMPILE" = true ]; then
  warn "Skipping compilation (--no-compile flag set)"
else
  step "Compiling PureEdgeSim..."
  "$MVN" clean compile -q \
    --no-transfer-progress \
    -f "$PROJECT_ROOT/pom.xml" \
    || err "Compilation failed — check the error output above."
  info "Compilation successful."
fi

# ── launch ────────────────────────────────────────────────────────────────────
step "Launching PureEdgeSim interactive menu..."
echo ""
echo -e "  ${YELLOW}Tip:${RESET} use options 2–6 in the menu to configure settings,"
echo -e "       then choose 1 to start the simulation."
echo ""

"$MVN" exec:java \
  -f "$PROJECT_ROOT/pom.xml" \
  -Dexec.mainClass="com.mechalikh.pureedgesim.MainApplication" \
  "${EXTRA_ARGS[@]/#/-Dexec.args=}" \
  2>&1 | grep -v "^\[INFO\] \(Scanning\|Building\|---\|BUILD\|Total\|Finished\)" \
       || true

echo ""
echo -e "${GREEN}${BOLD}  PureEdgeSim session ended.${RESET}"
echo ""
