package br.com.sttsoft.ticktzy.features.sale.ui

import br.com.sttsoft.ticktzy.presentation.base.BaseActivity

class SalesActivity : BaseActivity() {
    /*

    override val enablePrinterBinding = true
    override val enablePosPrinter = true

    private val binding by lazy { ActivitySaleBinding.inflate(layoutInflater) }
    private val vm: SaleViewModel by defaultVmFactory {
        SaleViewModel(
            productCache = ProductCacheUseCase(this),
            sitef = SitefUseCase(this),
            prefs = PrefsGatewayImpl(this)
        )
    }

    private lateinit var payLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        vm.setInfos(getFromPrefs("SITEF_INFOS"))
        vm.onEvent(SaleEvent.Load)

        binding.searchBar.addOnTextChangedListener { q -> vm.onEvent(SaleEvent.Search(q)) }

        binding.btnBack.setOnClickListener { finish() }
        binding.paymentBar.setOnPixClick { vm.onEvent(SaleEvent.PixPay) }
        binding.paymentBar.setOnCardClick {
            PaymentTypeChooseDialog({ t ->
                vm.onEvent(SaleEvent.CardPay(t)) // "debit"/"credit"
            }, false).show(supportFragmentManager, "CardTypeDialog")
        }
        binding.paymentBar.setOnCashClick {
            ChangeDialog(this, adapter.getTotal()) { recebido, troco, d ->
                // se quiser imprimir recibo dinheiro aqui (Android-specific), chame printer.printMoneyReceipt(...)
                vm.onEvent(SaleEvent.CashPay(recebido, troco))
                d.dismiss()
            }.show()
        }

        payLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            vm.onEvent(SaleEvent.PaymentResult(res.resultCode, res.data))
        }

        collectState(vm.state) { s ->
            if (s.loading) showLoading() else hideLoading()
            adapter = ProductAdapter(
                vm.onEvent(SaleEvent.UpdateProducts(adapter.getSelectedProducts())),
                { total -> binding.paymentBar.setTotalText(getString(R.string.text_sale_price).format(total)) })
            binding.rclProducts.adapter = adapter
            binding.paymentBar.setTotalText(getString(R.string.text_sale_price).format(s.total))
        }

        collectEffect(vm.effect) { eff ->
            when (eff) {
                is SaleEffect.PrintEstablishmentReceipt -> {
                    posmpReceiptController.printReceipt(
                        rawText = eff.estab,
                        onSuccess = { showToast("Impresso com sucesso") },
                        onError   = { showToast(it, true) }
                    )

                    val client = eff.client
                    if (!client.isNullOrBlank()) {
                        ConfirmDialog({ opt ->
                            if (opt == "yes") {
                                posmpReceiptController.printReceipt(
                                    rawText = client,
                                    onError = { showToast(it, true) }
                                )
                            }
                            printTickets()         // Sunmi (tickets)
                            vm.onTicketsPrinted()  // atualiza contadores por tipo
                        }, getString(R.string.dialog_print_question_title),
                            getString(R.string.dialog_print_question_body)
                        ).show(supportFragmentManager, "PrintQuestionDialog")
                    } else {
                        printTickets()
                        vm.onTicketsPrinted()
                    }
                }

                is SaleEffect.AskToPrintCustomerCopy -> {
                    ConfirmDialog({ opt ->
                        if (opt == "yes") {
                            posmpReceiptController.printReceipt(
                                rawText = eff.client,
                                onError = { showToast(it, true) }
                            )
                        }
                        printTickets()
                        vm.onTicketsPrinted()
                    }, getString(R.string.dialog_print_question_title),
                        getString(R.string.dialog_print_question_body)
                    ).show(supportFragmentManager, "PrintQuestionDialog")
                }

                is SaleEffect.LaunchPayment -> payLauncher.launch(eff.intent)
                is SaleEffect.Toast -> showToast(eff.message, eff.long)
                SaleEffect.PrintTickets -> { printTickets(); vm.onTicketsPrinted() }
                SaleEffect.Close -> finish()
            }
        }
    }

    private fun printTickets() {
        val info: InfoResponse? = vm.infos
        val prods = adapter.getSelectedProducts()
        if (info == null) return
        prods.forEach { p ->
            repeat(p.quantity) { printerController.printTicket(info, p.name, p.price) }
        }
    }
*/
}
