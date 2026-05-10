package com.thanhdv.workchecker.data

data class UserConfig(
    val name: String = "",
    val email: String = "",
    val employeeId: String = "",
    val webhookURL: String = "",
    val payload: String = """{"embeds":[{"color":5763719,"title":"✅ Check-in","description":"👤 **Nhân viên:** {name}\n🪪 **Mã NV:** {empId}\n📧 **Email:** {email}","timestamp":"{isoTime}","footer":{"text":"Work Checker"}}]}""",
    val message: String = "{name} ({empId}) checked at {time}",
)