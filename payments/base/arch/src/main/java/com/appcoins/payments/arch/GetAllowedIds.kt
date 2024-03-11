package com.appcoins.payments.arch

interface GetAllowedIds {
  suspend operator fun invoke(): Set<String>
}
