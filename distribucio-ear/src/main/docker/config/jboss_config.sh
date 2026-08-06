#!/bin/sh

# S'executa a l'arrancada del contenidor (enganxat a $JBOSS_HOME/bin/standalone.sh, veure Dockerfile.maven).
# Substitueix als fitxers de propietats de l'aplicació (jboss.properties/jboss_system.properties) els
# placeholders per el valor real de la variable d'entorn del mateix nom que la clau de la propietat
# (p.ex. la clau "es.caib.distribucio.plugin.arxiu.caib.base.url" es substitueix si existeix una
# variable d'entorn amb aquest mateix nom exacte -- veure docker-compose.yml, que defineix aquestes
# variables amb noms de propietat literals). Els subsistemes de standalone-openshift.xml (datasources,
# keycloak, mail...) ja resolen els seus propis "${env.JBOSS_*}" directament, sense passar per aquest
# script.

SCRIPT_DIR=$(dirname -- "$0")
JBOSS_PROPS_FILE=$SCRIPT_DIR/jboss.properties
JBOSS_SYSTEM_PROPS_FILE=$SCRIPT_DIR/jboss_system.properties
TEMP_PROPS_FILE=$SCRIPT_DIR/jboss_properties.tmp

echo "Substituint variables d'entorn als fitxers de propietats de distribucio..."
awk -F '=' 'NF {if (ENVIRON[$1]) {print $1 "=" ENVIRON[$1]} else {print $1 "=" $2}}' "$JBOSS_PROPS_FILE" > "$TEMP_PROPS_FILE" && mv "$TEMP_PROPS_FILE" "$JBOSS_PROPS_FILE"
awk -F '=' 'NF {if (ENVIRON[$1]) {print $1 "=" ENVIRON[$1]} else {print $1 "=" $2}}' "$JBOSS_SYSTEM_PROPS_FILE" > "$TEMP_PROPS_FILE" && mv "$TEMP_PROPS_FILE" "$JBOSS_SYSTEM_PROPS_FILE"
echo "...fitxers de propietats de distribucio actualitzats"
