smart-energy-esg-iot ⚡🏠🌱

API RESTful + Dashboard estático para monitorar energia & ocupação de ambientes (ESG/IoT).
Stack: Spring Boot + MongoDB Atlas + Chart.js. Inclui CRUD de devices, ingestão de telemetria e painel web simples.

🎯 Objetivo ESG: reduzir consumo de energia acionando luzes conforme presença e registrando telemetria para auditoria e eficiência.

✨ Features

📡 /api/telemetry: recebe leituras (presence, lightOn, temperature, ts)

🔎 /api/readings/latest: busca última leitura por sala

📈 Dashboard: GET / com gráfico de temperatura + status em tempo real

🧰 CRUD de devices: GET/POST/PUT/DELETE /api/devices

📜 Swagger UI: GET /swagger-ui.html

❤️ CORS liberado para DEV

🧱 Arquitetura (resumo)
ESP32/Wokwi → API (Spring Boot)
                 ↘ MongoDB Atlas (esg_iot_db)
Dashboard (index.html + JS) ← API (latest/history)


Coleções

devices: cadastro dos dispositivos

readings: leituras de telemetria
Índice recomendado: { room: 1, ts: -1 } (nome: idx_room_ts_desc)

🚀 Como rodar
1) Pré-requisitos

JDK 21

Maven (ou wrapper ./mvnw)

Conta no MongoDB Atlas (cluster -rev2 já OK)

2) Variável de ambiente
# Windows (PowerShell)
setx MONGO_URI "mongodb+srv://milton:SUA_SENHA@esg-iot-cluster-rev2.59g1ri9.mongodb.net/esg_iot_db?retryWrites=true&w=majority"
# reinicie o terminal/IDE


Também funciona com a URI direto no application.properties, mas evite commitar senhas.

3) Subir a aplicação
./mvnw spring-boot:run


Dashboard: http://localhost:8080/

Swagger: http://localhost:8080/swagger-ui.html

🔌 Endpoints principais
POST   /api/telemetry
GET    /api/readings/latest?room=Sala%20101
GET    /api/readings?room=Sala%20101&from=2025-10-25T12:00:00Z&to=2025-10-25T18:00:00Z
GET    /api/devices
POST   /api/devices
GET    /api/devices/{id}
PUT    /api/devices/{id}
DELETE /api/devices/{id}

Payload de telemetria (exemplo)
{
  "deviceId": "sala-101-meter",
  "room": "Sala 101",
  "presence": true,
  "lightOn": false,
  "temperature": 24.7,
  "ts": "2025-10-25T12:34:56Z"
}

Curl rápido
curl -X POST http://localhost:8080/api/telemetry \
  -H "Content-Type: application/json" \
  -d '{"deviceId":"sala-101-meter","room":"Sala 101","presence":true,"lightOn":true,"temperature":24.3}'

🗃️ Configuração do MongoDB (resumo)

Database: esg_iot_db

Collections: devices, readings

Índice em readings:

Fields: { "room": 1, "ts": -1 }

Options: { "name": "idx_room_ts_desc" }

Usuário Atlas: milton (senha definida por você)

IP Allowlist: 0.0.0.0/0 (DEV)

🖥️ Front-end estático (onde fica)

src/main/resources/templates/index.html

src/main/resources/static/assets/style.css

src/main/resources/static/assets/iot.js

src/main/resources/static/assets/ui.js

O painel envia leituras e atualiza o estado (Presença/Luz/Temperatura) a cada 3s.

🤖 Simulação IoT (ESP32)

🔧 Wokwi (web, grátis): simule um ESP32 enviando POST para http://<seu-ip>:8080/api/telemetry

🧪 MQTT opcional: (futuro) integrar com broker e um pequeno serviço de bridge HTTP

Sketch base (pseudocódigo):

// WiFiClient + HTTPClient
// POST em /api/telemetry com JSON igual ao payload acima


Se quiser, eu envio um sketch pronto para o Wokwi.

📊 ESG/KPIs sugeridos

🔌 kWh evitados por apagar luzes quando sem presença

🌡️ Temperatura média por sala (conforto térmico)

🕒 Tempo de ocupação vs. luz ligada (eficiência)

📈 Tendência por horário (picos vs. vales)

🧩 Commits sugeridos

chore: bootstrap Spring Boot (web, mongodb, validation, actuator, lombok)

docs: add Swagger UI and configure Mongo URI + CORS (dev)

feat: domain models (Device, Reading)

feat: repositories for Device and Reading

feat: REST controllers (devices CRUD, telemetry, latest, history)

feat: static dashboard (index + CSS + JS) and telemetry integration

🛡️ Segurança

Nunca commitar senhas (use MONGO_URI por env)

Depois do DEV, restringir IPs no Atlas

Habilitar logs/metrics com Actuator apenas em ambientes controlados

📜 Licença

MIT — use e adapte livremente.
Contribuições são bem-vindas! ✨

🤖 Simulação ESP32 (Wokwi) – passo a passo

Simula um ESP32 enviando leituras para POST /api/telemetry.
Use o Wokwi
 (grátis) ou o Arduino IDE.

Garanta a API rodando em http://<SEU_IP_LOCAL>:8080 (ou use http://127.0.0.1:8080 + Wokwi “HTTP” com ngrok se precisar expor).

No Wokwi escolha ESP32 e cole o sketch abaixo.

// ESP32 + HTTPClient (Arduino Core)
// Envia JSON para /api/telemetry a cada 5s

#include <WiFi.h>
#include <HTTPClient.h>

const char* WIFI_SSID = "Wokwi-GUEST";  // ou seu SSID
const char* WIFI_PASS = "";             // senha do Wi-Fi

// 👉 troque pelo IP/host onde sua API está rodando
String API_BASE = "http://192.168.0.10:8080";  // ex.: http://<seu-ip>:8080

// Config do “dispositivo”
String ROOM     = "Sala 101";
String DEVICEID = "sala-101-meter";

bool presence   = true;
bool lightOn    = true;
float tempC     = 24.0;

void setup() {
  Serial.begin(115200);
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  Serial.print("Conectando ao WiFi");
  while (WiFi.status() != WL_CONNECTED) { delay(500); Serial.print("."); }
  Serial.println("\nWiFi OK!");
}

void loop() {
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    String url = API_BASE + "/api/telemetry";
    http.begin(url);
    http.addHeader("Content-Type", "application/json");

    // Simples “oscilação” dos valores
    presence = !presence;
    lightOn  = presence ? true : lightOn;
    tempC   += (presence ? 0.2 : -0.1);

    // Monta JSON (ts opcional; se não enviar, server usa now())
    String payload = String("{") +
      "\"deviceId\":\"" + DEVICEID + "\"," +
      "\"room\":\""     + ROOM     + "\"," +
      "\"presence\":"   + (presence ? "true" : "false") + "," +
      "\"lightOn\":"    + (lightOn  ? "true" : "false") + "," +
      "\"temperature\":" + String(tempC, 1) +
    "}";

    int code = http.POST(payload);
    Serial.printf("POST %s -> %d\n", url.c_str(), code);
    Serial.println(payload);
    http.end();
  }
  delay(5000);
}


Dicas:

Se estiver no Wokwi e a API local não for acessível, exponha com ngrok:
ngrok http 8080 → use a URL pública do ngrok em API_BASE.

O painel web (/) vai refletir as leituras (botões + gráfico).

🧪 Seed de dados (opcional)
Opção A — via mongosh
use esg_iot_db
db.devices.insertMany([
  { name: "sala-101-meter", room: "Sala 101", createdAt: new Date() },
  { name: "sala-102-meter", room: "Sala 102", createdAt: new Date() }
])
db.readings.createIndex({ room: 1, ts: -1 }, { name: "idx_room_ts_desc" })

Opção B — via curl (sem mexer direto no banco)
curl -X POST http://localhost:8080/api/devices \
  -H "Content-Type: application/json" \
  -d '{"name":"sala-101-meter","room":"Sala 101"}'

curl -X POST http://localhost:8080/api/telemetry \
  -H "Content-Type: application/json" \
  -d '{"deviceId":"sala-101-meter","room":"Sala 101","presence":true,"lightOn":true,"temperature":24.3}'

🔒 Boas práticas rápidas

Não commite senhas: use spring.data.mongodb.uri=${MONGO_URI} e defina MONGO_URI no ambiente.

Em produção: restringir IP Allowlist no Atlas e desabilitar Actuator endpoints públicos.

## 👥 Colaboradores

- 🧑‍💻 **Hugo Correa Farranha** — RM **558215**
- 🧑‍💻 **Milton Ribeiro** — RM **556051**
- 🧑‍💻 **Victor Mazzola** — RM **557130**
