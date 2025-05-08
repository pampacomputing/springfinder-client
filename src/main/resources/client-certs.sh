#!/usr/bin/env bash
set -euo pipefail
export MSYS_NO_PATHCONV=1
export MSYS2_ARG_CONV_EXCL="*"

# Function to get the IP address
get_ip_address() {
    # Detect OS type
    OS=$(uname)

    if [[ "$OS" == "Linux" ]]; then
        # For Linux, use hostname -I
        IP_ADDRESS=$(hostname -I | awk '{print $1}')
    elif [[ "$OS" == "Darwin" ]]; then
        # For macOS, use ifconfig
        IP_ADDRESS=$(ifconfig en0 | grep inet | awk '{print $2}' | head -n 1)
    else
        # For Windows (using Git Bash), use ipconfig
        IP_ADDRESS=$(ipconfig | grep -A 5 "Ethernet adapter" | grep "IPv4" | head -n 1 | cut -d ':' -f2 | tr -d '[:space:]')
    fi
    echo "$IP_ADDRESS"
}

# Get the IP address
IP_ADDRESS=$(get_ip_address)

# Check if the IP address is empty or invalid
if [[ -z "$IP_ADDRESS" || ! "$IP_ADDRESS" =~ ^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "Invalid or empty IP address detected. Please enter your IP address manually:"
    read -p "Enter IP Address: " IP_ADDRESS
fi

echo "Using IP Address: $IP_ADDRESS"

keytool -genkeypair -alias springfinder-client -keyalg RSA -keysize 2048 -dname "CN=$IP_ADDRESS, OU=Pampa, O=Pampa, L=Sao Paulo, ST=Sao Paulo, C=BR" -ext SAN=IP:$IP_ADDRESS -validity 365 -keystore springfinder-client.jks -storepass springfinder

keytool -importcert -alias springfinder-server -file springfinder-server.crt -keystore client-truststore.jks -storetype JKS -storepass springfinder -noprompt
