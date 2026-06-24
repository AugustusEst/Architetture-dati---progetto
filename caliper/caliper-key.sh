#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

ORG1_USER_MSP="$ROOT_DIR/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp"
OUT_DIR="$ROOT_DIR/caliper/org1"

mkdir -p "$OUT_DIR"

if [ ! -d "$ORG1_USER_MSP" ]; then
  echo "Error: MSP User1 Org1 not found"
  echo "Before run:"
  echo "./network.sh up createChannel -ca -c mychannel"
  exit 1
fi

KEY_FILE="$(find "$ORG1_USER_MSP/keystore" -type f | head -n 1)"
CERT_FILE="$ORG1_USER_MSP/signcerts/cert.pem"

if [ -z "$KEY_FILE" ]; then
  echo "Error: private key not found in $ORG1_USER_MSP/keystore"
  exit 1
fi

cp "$KEY_FILE" "$OUT_DIR/user1-key.pem"
cp "$CERT_FILE" "$OUT_DIR/user1-cert.pem"

chmod 600 "$OUT_DIR/user1-key.pem"

echo "Caliper credentials copied in:"
echo "$OUT_DIR"
