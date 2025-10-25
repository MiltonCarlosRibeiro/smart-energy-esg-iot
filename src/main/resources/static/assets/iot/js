window.IOT = (function(){
  const base = "";

  async function sendTelemetry(reading){
    const res = await fetch(`${base}/api/telemetry`,{
      method:"POST",
      headers:{ "Content-Type":"application/json" },
      body: JSON.stringify({
        deviceId: reading.deviceId || "sala-101-meter",
        room: reading.room || "Sala 101",
        presence: !!reading.presence,
        lightOn: !!reading.lightOn,
        temperature: reading.temperature ?? 24.0
      })
    });
    if(!res.ok) throw new Error("telemetry failed");
    return res.json();
  }

  async function fetchLatest(room){
    const res = await fetch(`${base}/api/readings/latest?room=${encodeURIComponent(room)}`);
    if (res.status === 204) return null;
    if(!res.ok) throw new Error("latest failed");
    return res.json();
  }

  return { sendTelemetry, fetchLatest };
})();
