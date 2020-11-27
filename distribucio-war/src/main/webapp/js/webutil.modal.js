// Basat en http://stefangabos.ro/jquery/jquery-plugin-boilerplate-revisited/
(function($) {
	$.webutilModal = function(element, options) {
		var defaults = {
			adjustHeight: true,
			maximized: false,
			refreshMissatges: true,
			refreshDatatable: false,
			refreshPagina: false,
			refreshTancar: false,
			elementBotons: "#modal-botons",
			elementForm: "#modal-form",
			elementTancarData: "modal-cancel",
			elementRetorn: null
		}
		var $element = $(element), element = element;
		var plugin = this;
		plugin.settings = {}
		plugin.serverParams = [];
		plugin.init = function() {
			plugin.settings = $.extend(defaults, $element.data(), options);
			$element.click(function(event) {
				var elementPerEvaluar = $element;
				if (elementPerEvaluar.prop("tagName") == 'TR' && event.target.tagName != 'TD') {
					elementPerEvaluar = $(event.target.tagName);
				}
				var obrirNovaFinestra = false;
				var selectionEnabled = $(event.currentTarget.closest('table')).attr("data-selection-enabled")=='true';
				if ((elementPerEvaluar.attr('href') || elementPerEvaluar.data('href')) && elementPerEvaluar.data('toggle') == 'modal' 
					&& (!selectionEnabled || (selectionEnabled && $(event.target).closest('td').index()!=0))) {
					obrirNovaFinestra = true;
				}
				if (obrirNovaFinestra) {
					var href = elementPerEvaluar.attr('href');
					if (!href)
						href = elementPerEvaluar.data('href');
					if (event.which != 2) {
						var dataTableId = plugin.settings.datatableId;
						if (!dataTableId && elementPerEvaluar.closest('.dataTables_wrapper')) {
							dataTableId = $('table.dataTable', elementPerEvaluar.closest('.dataTables_wrapper')).attr('id');
						}
						if (dataTableId) {
							plugin.settings.refreshDatatable = true;
						}
						var modalDivId = (plugin.settings.modalId) ? plugin.settings.modalId : "modal_" + (new Date().getTime());
						var modalData = '';
						if (plugin.settings.maximized) {
							modalData += ' data-maximized="true"';
						}
						if ($('#' + modalDivId).length == 0 ) {
							$('body').append(
								'<div id="' + modalDivId + '"' + modalData + '>' +
								'	<div class="modal" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">' +
								'		<div class="modal-dialog modal-lg">' +
								'			<div class="modal-content">' +
								'				<div class="modal-header">' +
								'					<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
								'					<h4 class="modal-title"></h4>' +
								'				</div>' +
								'				<div class="modal-body" style="padding:0">' +
								'					<iframe frameborder="0" height="100" width="100%"></iframe>' +
								'					<div class="datatable-dades-carregant" style="text-align: center; padding-bottom: 100px;"><span class="fa fa-circle-o-notch fa-spin fa-3x"></span></div>' +
								'				</div>' +
								'				<div class="modal-footer"></div>' +
								'			</div>' +
								'		</div>' +
								'	</div>' +
								'</div>');
							elementPerEvaluar.data("modal-id", modalDivId);
							$('#' + modalDivId).webutilModalShow({
								adjustHeight: plugin.settings.adjustHeight,
								maximized: plugin.settings.maximized,
								refreshMissatges: plugin.settings.refreshMissatges,
								refreshDatatable: plugin.settings.refreshDatatable,
								refreshPagina: plugin.settings.refreshPagina,
								refreshTancar: plugin.settings.refreshTancar,
								elementBotons: plugin.settings.elementBotons,
								elementForm: plugin.settings.elementForm,
								elementTancarData: plugin.settings.elementTancarData,
								contentUrl: webutilUrlAmbPrefix(href, '/modal'),
								dataTableId: dataTableId,
								elementRetorn: plugin.settings.elementRetorn
							});
							
						} else {
							$('#' + modalDivId).webutilModalShow({
								adjustHeight: plugin.settings.adjustHeight,
								maximized: plugin.settings.maximized,
								refreshMissatges: plugin.settings.refreshMissatges,
								refreshDatatable: plugin.settings.refreshDatatable,
								refreshPagina: plugin.settings.refreshPagina,
								refreshTancar: plugin.settings.refreshTancar,
								elementBotons: plugin.settings.elementBotons,
								elementForm: plugin.settings.elementForm,
								elementTancarData: plugin.settings.elementTancarData,
								dataTableId: dataTableId,
								elementRetorn: plugin.settings.elementRetorn
							});
						}
						$('#' + modalDivId).data('elementRetorn', plugin.settings.elementRetorn);
						$('#' + modalDivId).on('hide.bs.modal', function(event) {
							if (!$(this).data('nohide')) {
								$('#frameModal').remove();
								var valorCodi = localStorage['relval_' + modalDivId];
								var nomElementRetorn = $(this).data('elementRetorn');
								if (nomElementRetorn != null && valorCodi != undefined && valorCodi != '') {
									$(nomElementRetorn).val(valorCodi);
									$(nomElementRetorn).trigger('blur');
								}
							} else {
								event.preventDefault();
								event.stopImmediatePropagation();
								return false; 
							}
						});
					} else {
						window.open(href, '_blank');
					}
					return false;
				}
			});
		}
		// Mètodes públics
		plugin.adjustHeight = function() {
			var modalId = $(element).data("modal-id");
			var $modalElement = $('#' + modalId);
			var iframe = $('iframe', $modalElement);
			var height = $modalElement.contents().find("html").height();
			$(iframe).parent().css('height', height + 17 + 'px');
			$(iframe).css('min-height', height + 'px');
			$modalElement.height(height + 17 + 'px');
		};
		// Mètodes privats
		// Inicialització del plugin
        plugin.init();
	}

	$.fn.webutilModalShow = function(settings) {
		return this.filter("div").each(function() {
			var modalobj = $('div.modal', this);
			if (!modalobj.data('modal-configurada')) {
				var iframe = $('iframe', modalobj);
				modalobj.on('show.bs.modal', function () {
					iframe.empty();
					if (settings.height)
						iframe.css('height', '' + settings.height + 'px');
					iframe.attr("src", settings.contentUrl);
					iframe.load(function() {
						$('.modal-body .datatable-dades-carregant').hide();
						if(!iframe.attr("hidden")){
							iframe.show();
						}
						// Copiar el titol de la modal
						var titol = $(this).contents().find("title").html();
						$('.modal-header h4', $(this).parent().parent()).html(titol);
						// Copiar botons
						$('.modal-footer', $(iframe).parent().parent()).empty();
						var dataBotons = $('body', $(iframe).contents()).data('modal-botons');
						var modalBotons = (dataBotons) ? $(dataBotons, $(iframe).contents()) : $(settings.elementBotons, $(iframe).contents());
						if (modalBotons.length) {
							$('.modal-footer *', $(this).parent().parent()).remove();
							$('.btn', modalBotons).each(function(index) {
								var element = $(this);
								var clon = element.clone();
								if (element.data(settings.elementTancarData)) {
									clon.on('click', function () {
										$(iframe).parent().parent().parent().parent().data(settings.elementTancarData, 'true');
										$(iframe).parent().parent().parent().parent().modal('hide');
										return false;
									});
								} else {
									if (!clon.data('nosubmit')) {
										clon.on('click', function () {
											iframe.hide();
											$('.modal-body .datatable-dades-carregant').css('padding-bottom', '0px');
											$('.modal-body .datatable-dades-carregant').css('padding-top', '60px');
											$('.modal-body .datatable-dades-carregant').show();
											element.click();
											return false;
										});
									}
								}
								$('.modal-footer', $(iframe).parent().parent()).append(clon);
							});
							modalBotons.hide();
						}
						// Evaluar URL del formulari
						var dataForm = $('body', $(iframe).contents()).data('modal-form');
						
						
						
						var modalForm = (dataForm) ? $(dataForm, $(iframe).contents()) : $(settings.elementForm, $(iframe).contents());
						if (modalForm.length) {
							modalForm.attr('action', webutilUrlAmbPrefix(modalForm.attr('action'), '/modal'));
						}
						if (settings.maximized) {
							// Maximitzar
							$('.modal-dialog', modalobj).css('width', '98%');
							$('.modal-dialog', modalobj).css('top', '10px');
							$('.modal-dialog', modalobj).css('margin', 'auto');
							iframe.attr('height', '100%');
							var contentHeight = $(iframe).contents().find("html").outerHeight();
							var modalobj = $(iframe).parent().parent().parent();
							var taraModal = $('.modal-header', modalobj).outerHeight() + $('.modal-footer', modalobj).outerHeight();
							var maxBodyHeight = $(window.top).height() - taraModal - 20;
							$(iframe).height(maxBodyHeight + 'px');
							$('.modal-body', modalobj).css('height', maxBodyHeight + 'px');
							$(iframe).contents().find("body").css('height', maxBodyHeight + 'px');
						}
						webutilModalAdjustHeight(iframe);
					});
				});
				
				modalobj.on('hidden.bs.modal', function () {
					if (settings.refreshTancar) {
						$('#' + settings.dataTableId).webutilDatatable('refresh');
					}
				});
				
				iframe.on('load', function () {
					localStorage['relval_' + settings.dataTableId] = undefined;
//					$(this).removeData('retval');
					var pathname = this.contentDocument.location.pathname;
					if (pathname == webutilModalTancarPath()) {
						$('button.close', $(this).closest('.modal-dialog')).trigger('click');
						if (settings.refreshMissatges && !settings.refreshPagina) {
							webutilRefreshMissatges();
						}
						if (settings.refreshDatatable) {
							$('#' + settings.dataTableId).webutilDatatable('refresh');
						}
						if (settings.refreshPagina) {
							window.location.reload(true);
						}
					}
				});
				modalobj.data('modal-configurada', true);
			}
			$('.modal-body iframe *', modalobj).remove();
			$('.modal-footer *', modalobj).remove();
			modalobj.modal('show');
		});
	};

	$.fn.webutilModal = function(options) {
		var pluginName = 'webutilModal';
        return this.each(function() {
            if (undefined == $(this).data(pluginName)) {
                var plugin = new $.webutilModal(this, options);
                $(this).data(pluginName, plugin);
            } else if (options && typeof options !== 'object') {
            }
        });
    }

	$.fn.webutilModalEval = function() {
		$('[data-toggle="modal"]', this).each(function() {
			if (!$(this).attr('data-modal-eval')) {
				$(this).webutilModal();
				$(this).attr('data-modal-eval', 'true');
			}
		});
	}

	$(document).ready(function() {
		$('[data-toggle="modal"]', $(this)).each(function() {
			if (!$(this).attr('data-modal-eval')) {
				$(this).webutilModal();
				$(this).attr('data-modal-eval', 'true');
			}
		});
	});
	

}(jQuery));