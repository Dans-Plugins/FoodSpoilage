#!/bin/bash

SERVER_DIR="/testmcserver"
BUILD_DIR="/testmcserver-build"
RESOURCES_DIR="/resources/jars"

# Function: Log a message with the [POST-CREATE] prefix
log() {
    local message="$1"
    echo "[POST-CREATE] $message"
}

# Function: Setup server
setup_server() {
    if [ -z "$(ls -A "$SERVER_DIR")" ] || [ "$OVERWRITE_EXISTING_SERVER" = "true" ]; then
            rm -rf "$SERVER_DIR"/*
        cp "$BUILD_DIR"/spigot-"${MINECRAFT_VERSION}".jar "$SERVER_DIR"/spigot-"${MINECRAFT_VERSION}".jar
        mkdir "$SERVER_DIR"/plugins
    else
        log "Server is already set up."
    fi
}

# Function: Setup ops.json file
setup_ops_file() {
    log "Creating ops.json file..."
    cat <<EOF > /testmcserver/ops.json
    [
      {
        "uuid": "${OPERATOR_UUID}",
        "name": "${OPERATOR_NAME}",
        "level": ${OPERATOR_LEVEL},
        "bypassesPlayerLimit": false
      }
    ]
EOF
}

# Function: Accept EULA
accept_eula() {
    log "Accepting Minecraft EULA..."
    echo "eula=true" > "$SERVER_DIR"/eula.txt
}

# Function: Copy the latest plugin JAR with timestamp check
copy_latest_plugin_jar() {
    log "Copying the latest plugin JAR..."
    ls -l "$BUILD_DIR/FoodSpoilage/build" || log "ERROR: No build directory found in FoodSpoilage build directory."
    if [ ! -d "$BUILD_DIR/FoodSpoilage/build" ]; then
        log "ERROR: The build directory does not exist in the FoodSpoilage build directory."
        log "List of files in the build directory:"
        ls -l "$BUILD_DIR" || log "ERROR: Failed to list files in the build directory."
        return 1
    fi
    local jarFile=$(find "$BUILD_DIR/FoodSpoilage/build/libs" -name "FoodSpoilage-*.jar" -type f -print -quit)

    if [ -z "$jarFile" ]; then
        log "ERROR: No plugin JAR file found in the build directory."
        return 1
    fi

    local currentDate=$(date +%s)
    local jarDate=$(stat -c %Y "$jarFile")
    local diff=$((currentDate - jarDate))

    if [ $diff -gt 300 ]; then
        log "WARNING: The plugin JAR is older than 5 minutes. It may be necessary to rebuild the plugin."
    fi

    cp "$jarFile" "$SERVER_DIR/plugins" || log "ERROR: Failed to copy the plugin JAR."
}

# Function: Update Bluemap configuration
update_bluemap_config() {
    log "Updating Bluemap configuration..."
    sed -i 's/accept-download: false/accept-download: true/g' "$SERVER_DIR"/plugins/bluemap/core.conf
}

# Function: Start server
start_server() {
    log "Starting server ${MINECRAFT_VERSION} server from ${SERVER_DIR}/spigot-${MINECRAFT_VERSION}.jar..."
    java -jar "$SERVER_DIR"/spigot-"${MINECRAFT_VERSION}".jar
}

# Main Process
log "Running 'post-create.sh' script..."
setup_server
setup_ops_file
accept_eula
if ! copy_latest_plugin_jar; then
    log "Exiting script due to error in copying the latest plugin JAR."
    exit 1
fi

# Start Server
start_server