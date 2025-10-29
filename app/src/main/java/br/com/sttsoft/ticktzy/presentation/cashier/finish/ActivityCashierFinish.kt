package br.com.sttsoft.ticktzy.presentation.cashier.finish

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.sttsoft.ticktzy.R
import br.com.sttsoft.ticktzy.databinding.ActivityCashierFinishBinding
import br.com.sttsoft.ticktzy.domain.PrinterUseCase
import br.com.sttsoft.ticktzy.extensions.getPref
import br.com.sttsoft.ticktzy.extensions.savePref
import br.com.sttsoft.ticktzy.extensions.toReal
import br.com.sttsoft.ticktzy.extensions.toRealFormatado
import br.com.sttsoft.ticktzy.presentation.base.BaseActivity
import br.com.sttsoft.ticktzy.presentation.cashier.finish.components.TableInfosAdapter
import br.com.sttsoft.ticktzy.presentation.cashier.finish.components.tableInfos
import br.com.sttsoft.ticktzy.presentation.cashier.start.ActivityCashierStart
import br.com.sttsoft.ticktzy.presentation.dialogs.ConfirmDialog

class ActivityCashierFinish : BaseActivity() {
    override val enablePrinterBinding = true

    private val binding: ActivityCashierFinishBinding by lazy {
        ActivityCashierFinishBinding.inflate(layoutInflater)
    }

    private val itens: MutableList<tableInfos> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        showLoading()

        loadData()
        setAdapter()
        setButtonClick()
    }

    private fun loadData() {
        // itens.add(tableInfos.tableSection(getString(R.string.text_cashier_table_finish)))
        itens.add(
            tableInfos.tableRow(
                getString(R.string.text_cashier_table_sales_made),
                this.getPref("SALES_MADE", 0).toString(),
            ),
        )
        itens.add(
            tableInfos.tableRow(
                getString(R.string.text_cashier_table_charge_made),
                this.getPref("CHARGE_MADE", 0).toString(),
            ),
        )
        itens.add(
            tableInfos.tableRow(
                getString(R.string.text_cashier_table_sangria_made),
                this.getPref("SANGRIA_MADE", 0).toString(),
            ),
        )
        itens.add(
            tableInfos.tableRow(
                getString(R.string.text_cashier_table_reinforce_made),
                this.getPref("REINFORCE_MADE", 0).toString(),
            ),
        )
        itens.add(
            tableInfos.tableRow(
                getString(R.string.text_cashier_table_cancels_made),
                this.getPref("CANCELS_MADE", 0).toString(),
            ),
        )
        itens.add(tableInfos.tableSection(getString(R.string.text_cashier_table_payment_types)))

        itens.add(
            tableInfos.tableRow(
                getString(R.string.text_cashier_table_type_debit),
                String.format(
                    getString(R.string.text_cashier_table_value),
                    this.getPref("DEBIT_TYPE", 0).toString(),
                    this.getPref("DEBIT_VALUE", 0.0).toReal(),
                ),
            ),
        )

        itens.add(
            tableInfos.tableRow(
                getString(R.string.text_cashier_table_type_credit),
                String.format(
                    getString(R.string.text_cashier_table_value),
                    this.getPref("CREDIT_TYPE", 0).toString(),
                    this.getPref("CREDIT_VALUE", 0.0).toReal(),
                ),
            ),
        )

        itens.add(
            tableInfos.tableRow(
                getString(R.string.text_cashier_table_type_pix),
                String.format(
                    getString(R.string.text_cashier_table_value),
                    this.getPref("PIX_TYPE", 0).toString(),
                    this.getPref("PIX_VALUE", 0.0).toReal(),
                ),
            ),
        )

        itens.add(
            tableInfos.tableRow(
                getString(R.string.text_cashier_table_type_money),
                String.format(
                    getString(R.string.text_cashier_table_value),
                    this.getPref("MONEY_TYPE", 0).toString(),
                    this.getPref("MONEY_VALUE", 0.0).toReal(),
                ),
            ),
        )

        itens.add(tableInfos.tableSection(getString(R.string.text_cashier_table_final_values)))
        itens.add(
            tableInfos.tableRow(
                getString(R.string.text_cashier_table_start_money),
                this.getPref("CAIXA_INICIAL", 0L).toRealFormatado(),
            ),
        )
        itens.add(
            tableInfos.tableRow(
                getString(R.string.text_cashier_table_sangria_money),
                "- " + this.getPref("CAIXA_SANGRIA", 0L).toRealFormatado(),
            ),
        )
        itens.add(
            tableInfos.tableRow(
                getString(R.string.text_cashier_table_reinforce_money),
                "+ " + this.getPref("CAIXA_REINFORCE", 0L).toRealFormatado(),
            ),
        )
        itens.add(
            tableInfos.tableRow(
                getString(R.string.text_cashier_table_finish_money),
                this.getPref("CAIXA", 0L).toRealFormatado(),
            ),
        )
    }

    private fun setAdapter() {
        val adapter = TableInfosAdapter(itens)
        binding.rcvInfos.layoutManager = LinearLayoutManager(this)
        binding.rcvInfos.adapter = adapter

        hideLoading()
    }

    private fun setButtonClick() {
        binding.ivClose.setOnClickListener {
            finish()
        }

        binding.clTitle.setOnClickListener {
            finish()
        }

        binding.llConfirm.setOnClickListener {
            val dialog =
                ConfirmDialog({ option ->
                    when (option) {
                        "yes" -> {
                            PrinterUseCase(sunmiPrinterService).printFinish(this)

                            clearInformations()

                            val intent = Intent(this, ActivityCashierStart::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        "no" -> {}
                    }
                }, getString(R.string.text_cashier_title_finish), getString(R.string.text_cashier_finish))
            dialog.show(supportFragmentManager, "ConfirmDialog")
        }
    }

    fun clearInformations() {
        this.savePref("SALES_MADE", 0)
        this.savePref("CHARGE_MADE", 0)
        this.savePref("SANGRIA_MADE", 0)
        this.savePref("REINFORCE_MADE", 0)
        this.savePref("CANCELS_MADE", 0)
        this.savePref("DEBIT_TYPE", 0)
        this.savePref("CREDIT_TYPE", 0)
        this.savePref("PIX_TYPE", 0)
        this.savePref("MONEY_TYPE", 0)
        this.savePref("DEBIT_VALUE", 0.0)
        this.savePref("CREDIT_VALUE", 0.0)
        this.savePref("PIX_VALUE", 0.0)
        this.savePref("MONEY_VALUE", 0.0)
        this.savePref("CAIXA_SANGRIA", 0L)
        this.savePref("CAIXA_REINFORCE", 0L)
        this.savePref("CAIXA", 0L)
    }
}
