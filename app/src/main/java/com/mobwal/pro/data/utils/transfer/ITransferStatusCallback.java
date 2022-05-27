package com.mobwal.pro.data.utils.transfer;

public interface ITransferStatusCallback {
    /**
     * обработчик запуска операции
     * @param tid идентификатор транзакции
     * @param transfer обработчик передачи данных
     */
    void onStartTransfer(String tid, Transfer transfer);

    /**
     * обработчик перезапуска операции
     * @param tid идентификатор транзакции
     * @param transfer обработчик передачи данных
     */
    void onRestartTransfer(String tid, Transfer transfer);

    /**
     * обработчик выполнения задания
     * @param progress прогресс
     */
    void onPercentTransfer(String tid, TransferProgress progress, Transfer transfer);

    /**
     * обработчик остановки операции
     * @param tid идентификатор транзакции
     * @param transfer обработчик передачи данных
     */
    void onStopTransfer(String tid, Transfer transfer);

    /**
     * обработчик завершения операции
     * @param tid идентификатор транзакции
     * @param transfer обработчик передачи данных
     * @param data дополнительные данные
     */
    void onEndTransfer(String tid, Transfer transfer, Object data);

    /**
     * обработчик возникновения ошибки
     * @param tid идентификатор транзакции
     * @param message текст сообщения
     * @param transfer обработчик передачи данных
     */
    void onErrorTransfer(String tid, String message, Transfer transfer);
}
