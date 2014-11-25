/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lmo.utils.http;

/**
 *
 * @author developer
 */
public interface HttpListener {

    void onRead(int b);

    void onClose();
}
